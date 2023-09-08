package com.gundam.gdapi.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.gundam.gdapi.common.ErrorCode;

import com.gundam.gdapi.common.ResultUtils;
import com.gundam.gdapi.common.SmsLimiter;
import com.gundam.gdapi.constant.CommonConstant;
import com.gundam.gdapi.constant.UserConstant;
import com.gundam.gdapi.exception.ThrowUtils;
import com.gundam.gdapi.model.dto.user.UserQueryRequest;
import com.gundam.gdapi.model.dto.user.UserRegisterRequest;
import com.gundam.gdapi.model.dto.user.UserUpdateRequest;
import com.gundam.gdapi.model.enums.UserRoleEnum;
import com.gundam.gdapi.model.vo.LoginUserVO;
import com.gundam.gdapi.model.vo.UserDevKeyVO;
import com.gundam.gdapi.model.vo.UserVO;
import com.gundam.gdapi.utils.LeakyBucket;
import com.gundam.gdapi.utils.QiniuUtils;
import com.gundam.gdapi.utils.SqlUtils;
import com.gundam.gdapi.exception.BusinessException;
import com.gundam.gdapi.mapper.UserMapper;
import com.gundam.gdapi.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gundam.gdapi.utils.UserHolder;
import com.gundam.gdapicommon.model.entity.SmsMessage;
import com.gundam.gdapicommon.model.entity.User;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.gundam.gdapi.constant.CommonConstant.SALT;
import static com.gundam.gdapi.constant.UserConstant.*;
import static com.gundam.gdapi.utils.LeakyBucket.loginLeakyBucket;
import static com.gundam.gdapi.utils.LeakyBucket.registerLeakyBucket;
import static com.gundam.gdapicommon.constant.RabbitmqConstant.EXCHANGE_SMS_INFORM;
import static com.gundam.gdapicommon.constant.RabbitmqConstant.ROUTINGKEY_SMS;
import static com.gundam.gdapicommon.constant.RedisConstant.LOGINCODEPRE;

/**
 * 用户服务实现
 * @author Gundam
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private SmsLimiter smsLimiter;

    @Resource
    private Gson gson;

    private static final String CAPTCHA_PREFIX = "api:captchaId:";

    //登录和注册的标识，方便切换不同的令牌桶来限制验证码发送
    private static final String LOGIN_SIGN = "login";

    private static final String REGISTER_SIGN="register";

    public static final String USER_LOGIN_EMAIL_CODE ="user:login:email:code:";
    public static final String USER_REGISTER_EMAIL_CODE ="user:register:email:code:";

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest, HttpServletRequest request) {

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String vipCode = userRegisterRequest.getVipCode();
        String captcha = userRegisterRequest.getCaptcha();


        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, vipCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空!");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度过短!");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短!");
        }
        if (vipCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "vip编号过长!");
        }

        //账户不能包含特殊字符
        String regExp = "^[\\w_]{6,20}$";
        Matcher matcher = Pattern.compile(regExp).matcher(userAccount);
        if (!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户不能包含特殊字符!");
        }

        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致!");
        }

//        //手机号合法
//        if (!authPhoneNumber.isPhoneNum(mobile)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号非法!");
//        }



        String picCaptcha = stringRedisTemplate.opsForValue().get(CAPTCHA_PREFIX + request.getHeader("signature"));
        if (picCaptcha == null || !captcha.equals(picCaptcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图形验证码错误或已经过期，请重新刷新验证码!");
        }

//        // 手机号和验证码是否匹配
//        boolean verify = smsLimiter.verifyCode(mobile, code);
//        if (!verify) {
//            throw new BusinessException(ErrorCode.SMS_CODE_ERROR);
//        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("userAccount", userAccount);
            long count1 = this.baseMapper.selectCount(queryWrapper1);
            if (count1 > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复!");
            }
            // vip编号不能重复
            QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("vipCode", vipCode);
            long count2 = this.baseMapper.selectCount(queryWrapper2);
            if (count2 > 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"vip编号不能重复!");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配accessKey secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, user);


        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        //生成token
        String token = UUID.randomUUID().toString();;
        //加盐生成tokenKey
        String tokenKey = LOGIN_USER_KEY + SALT + token + loginUserVO.getId();

        session.setAttribute("token", tokenKey);

        //将用户信息保存在redis
        String id = String.valueOf(loginUserVO.getId());
        stringRedisTemplate.opsForValue().set(tokenKey, id);
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES); //设置过期时间

        loginUserVO.setUserToken(token);

        return loginUserVO;
    }



    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        //从请求头中获取token
        String token = request.getHeader("x-auth-token");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String userToken = SALT + token + currentUser.getId();
        String tokenKey = LOGIN_USER_KEY + userToken;

        String userId = stringRedisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }


        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        //long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }

        //从请求头中获取token
        String token = request.getHeader("x-auth-token");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String userToken = SALT + token + currentUser.getId();
        String tokenKey = LOGIN_USER_KEY + userToken;

        String userId = stringRedisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        //long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        request.getSession().setAttribute(USER_LOGIN_STATE, currentUser);
        //return this.getById(userId);
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
         return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        try {
            if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
            }

            HttpSession session = request.getSession();
            // 移除登录态
            session.removeAttribute(UserConstant.USER_LOGIN_STATE);
            String token = String.valueOf(session.getAttribute("token"));
            stringRedisTemplate.delete(token);
            session.removeAttribute("token");
        } catch (BusinessException e) {
            e.printStackTrace();
        } finally {
            // 从线程中移除用户
            UserHolder.removeUser();
        }

        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        Integer role = userQueryRequest.getRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(role != null, "role", role);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }



    @Override
    public void sendCode(String email, String captchaType) {


        if (StringUtils.isBlank(captchaType)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码类型为空!!!");
        }

        //令牌桶算法实现短信接口的限流，因为手机号码重复发送短信，要进行流量控制
        //解决同一个手机号的并发问题，锁的粒度非常小，不影响性能。只是为了防止用户第一次发送短信时的恶意调用
        synchronized (email.intern()) {
            Boolean exist = stringRedisTemplate.hasKey(USER_LOGIN_EMAIL_CODE +email);
            if (exist!=null && exist) {
                //1.令牌桶算法对手机短信接口进行限流 具体限流规则为同一个手机号，60s只能发送一次
                long lastTime=0L;
                LeakyBucket leakyBucket = null;
                if (captchaType.equals(REGISTER_SIGN)){
                    String strLastTime = stringRedisTemplate.opsForValue().get(USER_REGISTER_EMAIL_CODE + email);
                    if (strLastTime!=null){
                        lastTime = Long.parseLong(strLastTime);
                    }
                    leakyBucket = registerLeakyBucket;
                }else{
                    String strLastTime = stringRedisTemplate.opsForValue().get(USER_LOGIN_EMAIL_CODE + email);
                    if (strLastTime!=null){
                        lastTime = Long.parseLong(strLastTime);
                    }
                    leakyBucket = loginLeakyBucket;
                }

                if (!leakyBucket.control(lastTime)) {
                    log.info("邮箱发送太频繁了");
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱送太频繁了");
                }
            }

            //2.符合限流规则则生成手机短信
            String code = RandomUtil.randomNumbers(4);
            SmsMessage smsMessage = new SmsMessage(email, code);


            //消息队列异步发送短信，提高短信的吞吐量
            rabbitTemplate.convertAndSend(EXCHANGE_SMS_INFORM,ROUTINGKEY_SMS,smsMessage);

            log.info("邮箱对象："+smsMessage.toString());
            //更新手机号发送短信的时间
            if (captchaType.equals(REGISTER_SIGN)){
                stringRedisTemplate.opsForValue().set(USER_REGISTER_EMAIL_CODE +email,""+System.currentTimeMillis()/1000);
            }else {
                stringRedisTemplate.opsForValue().set(USER_LOGIN_EMAIL_CODE +email,""+System.currentTimeMillis()/1000);
            }

        }

    }


    @Override
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        //前端必须传一个 signature 来作为唯一标识
        String signature = request.getHeader("signature");
        if (StringUtils.isEmpty(signature)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        try {
            // 自定义纯数字的验证码（随机4位数字，可重复）
            RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
            LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100, 30);
            lineCaptcha.setGenerator(randomGenerator);
            //设置响应头
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            // 输出到页面
            lineCaptcha.write(response.getOutputStream());
            // 打印日志
            log.info("captchaId：{} ----生成的验证码:{}", signature, lineCaptcha.getCode());
            // 将验证码设置到Redis中,2分钟过期
            stringRedisTemplate.opsForValue().set(CAPTCHA_PREFIX + signature, lineCaptcha.getCode(), 2, TimeUnit.MINUTES);
            // 关闭流
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public UserDevKeyVO genkey(HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        UserDevKeyVO userDevKeyVO = genKey(loginUser.getUserAccount());
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userAccount",loginUser.getUserAccount());
        updateWrapper.eq("id",loginUser.getId());
        updateWrapper.set("accessKey",userDevKeyVO.getAccessKey());
        updateWrapper.set("secretKey",userDevKeyVO.getSecretKey());
        this.update(updateWrapper);
        loginUser.setAccessKey(userDevKeyVO.getAccessKey());
        loginUser.setSecretKey(userDevKeyVO.getSecretKey());

        //重置登录用户的ak,sk信息

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(loginUser, loginUserVO);
        // 生成token
        String token = UUID.randomUUID().toString();
        // 加盐生成tokenKey
        String tokenKey = LOGIN_USER_KEY + SALT + token + loginUserVO.getId();
        // 3. 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, loginUserVO);
        session.setAttribute("token", tokenKey);
        // 将用户保存在redis
        String id = String.valueOf(loginUserVO.getId());
        stringRedisTemplate.opsForValue().set(tokenKey, id);
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES); // 设置过期时间


        return userDevKeyVO;
    }

    @Override
    public LoginUserVO userLoginBySms(String emailNum, String emailCode, HttpServletRequest request, HttpServletResponse response) {

        //1.校验邮箱验证码是否正确
        if (!emailCodeValid(emailNum, emailCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱验证码错误!!!");
        }

        //2.校验邮箱是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",emailNum);
        User user = this.getOne(queryWrapper);

        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在！");
        }

        // 3. 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, user);


        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        //生成token
        String token = UUID.randomUUID().toString();;
        //加盐生成tokenKey
        String tokenKey = LOGIN_USER_KEY + SALT + token + loginUserVO.getId();

        session.setAttribute("token", tokenKey);

        //将用户信息保存在redis
        String id = String.valueOf(loginUserVO.getId());
        stringRedisTemplate.opsForValue().set(tokenKey, id);
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES); //设置过期时间

        loginUserVO.setUserToken(token);

        return loginUserVO;
    }


    @Override
    public long userEmailRegister(String emailNum, String emailCaptcha) {
        if (!emailCodeValid(emailNum, emailCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱格式或邮箱验证码错误!!!");
        }

        //2.校验邮箱是否已经注册过
        synchronized (emailNum.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email",emailNum);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱已经注册过了！！！账号重复");
            }

            //给用户分配调用接口的公钥和私钥ak,sk，保证复杂的同时要保证唯一
            String accessKey = DigestUtil.md5Hex(SALT+emailNum+ RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT+emailNum+ RandomUtil.randomNumbers(8));

            // 3. 插入数据
            User user = new User();
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserAccount(emailNum);
            user.setEmail(emailNum);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public boolean uploadFileAvatar(MultipartFile file, HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);

        //更新持久层用户头像信息
        User updateUser = new User();
        updateUser.setId(loginUser.getId());
        String url = null;
        try {
            url = "http://api.ggaoda.cn/" + QiniuUtils.QiniuCloudUploadImage(file).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        updateUser.setAvatarUrl(url);
        boolean result = this.updateById(updateUser);

        //更新用户缓存
        loginUser.setAvatarUrl(url);


        // 3. 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);


        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(loginUser, loginUserVO);

        //生成token
        String token = UUID.randomUUID().toString();;
        //加盐生成tokenKey
        String tokenKey = LOGIN_USER_KEY + SALT + token + loginUserVO.getId();

        session.setAttribute("token", tokenKey);

        //将用户信息保存在redis
        String id = String.valueOf(loginUserVO.getId());
        stringRedisTemplate.opsForValue().set(tokenKey, id);
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES); //设置过期时间

        loginUserVO.setUserToken(token);

        return result;
    }

    @Override
    public boolean updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {

        //允许用户修改自己的信息，但拒绝用户修改别人的信息；但管理员可以修改别人的信息
        User loginUser = this.getLoginUser(request);
        Long id = userUpdateRequest.getId();
        if (!loginUser.getId().equals(id)){
            if (!loginUser.getRole().equals(UserRoleEnum.ADMIN.getValue())){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = this.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        //修改完要更新用户缓存
        loginUser.setUsername(userUpdateRequest.getUsername());
        loginUser.setGender(userUpdateRequest.getGender());
        // 3. 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, user);


        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        //生成token
        String token = UUID.randomUUID().toString();;
        //加盐生成tokenKey
        String tokenKey = LOGIN_USER_KEY + SALT + token + loginUserVO.getId();

        session.setAttribute("token", tokenKey);

        //将用户信息保存在redis
        stringRedisTemplate.opsForValue().set(tokenKey, String.valueOf(id));
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES); //设置过期时间

        loginUserVO.setUserToken(token);

        return true;
    }



    private UserDevKeyVO genKey(String userAccount){
        String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
        UserDevKeyVO userDevKeyVO = new UserDevKeyVO();
        userDevKeyVO.setAccessKey(accessKey);
        userDevKeyVO.setSecretKey(secretKey);
        return userDevKeyVO;
    }

    /**
     * 邮箱验证码校验
     * @param emailNum
     * @param emailCode
     * @return
     */
    private boolean emailCodeValid(String emailNum, String emailCode) {
        String code = stringRedisTemplate.opsForValue().get(LOGINCODEPRE + emailNum);
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式或邮箱验证码错误!!!");
        }

        if (!emailCode.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式或邮箱验证码错误!!!");
        }

        return true;
    }



}
