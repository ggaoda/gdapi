package com.gundam.gdapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gundam.gdapi.model.dto.user.UserQueryRequest;
import com.gundam.gdapi.model.dto.user.UserRegisterRequest;
import com.gundam.gdapi.model.dto.user.UserUpdateRequest;
import com.gundam.gdapi.model.vo.LoginUserVO;
import com.gundam.gdapi.model.vo.UserDevKeyVO;
import com.gundam.gdapi.model.vo.UserVO;
import com.gundam.gdapicommon.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户服务
 * @author Gundam
 */
public interface UserService extends IService<User> {

//    /**
//     * 用户注册
//     *
//     * @param userAccount   用户账户
//     * @param userPassword  用户密码
//     * @param checkPassword 校验密码
//     * @return 新用户 id
//     */
//    long userRegister(String userAccount, String userPassword, String checkPassword, String vipCode);
      long userRegister(UserRegisterRequest userRegisterRequest, HttpServletRequest request);
    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 判断是否为管理员
     *
     * @param request 请求
     * @return 是否
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return  是否
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息列表
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 发送验证码
     * @param email
     * @param captchaType
     */
    public void sendCode(String email, String captchaType);


    /**
     * 生成图像验证码
     * @param request
     * @param response
     */
    void getCaptcha(HttpServletRequest request, HttpServletResponse response);


    /**
     * 重新生成ak，sk
     * @param request
     * @return
     */
    UserDevKeyVO genkey(HttpServletRequest request);

    /**
     * 使用邮箱登录(后续会改造成使用手机号登录)
     * @param emailNum
     * @param emailCode
     * @param request
     * @param response
     * @return
     */
    LoginUserVO userLoginBySms(String emailNum, String emailCode, HttpServletRequest request, HttpServletResponse response);

    /**
     * 使用邮箱注册(后续会改造成使用手机号注册)
     * @param emailNum
     * @param emailCaptcha
     * @return
     */
    long userEmailRegister(String emailNum, String emailCaptcha);

    /**
     * 上传用户头像
     * @param file
     * @param request
     * @return
     */
    boolean uploadFileAvatar(MultipartFile file, HttpServletRequest request);

    /**
     * 更新用户
     * @param userUpdateRequest
     * @param request
     * @return
     */
    boolean updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request);


}
