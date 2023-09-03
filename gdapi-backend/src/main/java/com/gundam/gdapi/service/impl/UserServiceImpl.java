package com.gundam.gdapi.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gundam.gdapi.common.ErrorCode;

import com.gundam.gdapi.common.SmsLimiter;
import com.gundam.gdapi.constant.CommonConstant;
import com.gundam.gdapi.constant.UserConstant;
import com.gundam.gdapi.model.dto.user.UserQueryRequest;
import com.gundam.gdapi.model.dto.user.UserRegisterRequest;
import com.gundam.gdapi.model.enums.UserRoleEnum;
import com.gundam.gdapi.model.vo.LoginUserVO;
import com.gundam.gdapi.model.vo.UserVO;
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
import com.gundam.gdapicommon.AuthPhoneNumber;
import com.gundam.gdapicommon.model.entity.User;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.gundam.gdapi.constant.CommonConstant.SALT;
import static com.gundam.gdapi.constant.UserConstant.*;

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
    private SmsLimiter smsLimiter;

    private static final String CAPTCHA_PREFIX = "api:captchaId:";

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest, HttpServletRequest request) {

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String vipCode = userRegisterRequest.getVipCode();
        String mobile = userRegisterRequest.getMobile();
        String captcha = userRegisterRequest.getCaptcha();
        String code = userRegisterRequest.getCode();

        AuthPhoneNumber authPhoneNumber = new AuthPhoneNumber();

        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, vipCode, mobile, captcha, code)) {
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

        //手机号合法
        if (!authPhoneNumber.isPhoneNum(mobile)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号非法!");
        }

        //图形验证码是否正确
        String signature = request.getHeader("signature");
        if (signature == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图形验证码为空!");
        }

        String picCaptcha = stringRedisTemplate.opsForValue().get(CAPTCHA_PREFIX + signature);
        if (picCaptcha == null || authPhoneNumber.isCaptcha(captcha) || !captcha.equals(picCaptcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图形验证码错误或已经过期，请重新刷新验证码!");
        }

        // 手机号和验证码是否匹配
        boolean verify = smsLimiter.verifyCode(mobile, code);
        if (!verify) {
            throw new BusinessException(ErrorCode.SMS_CODE_ERROR);
        }

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


}
