package com.zyf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyf.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author zyf
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 检验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 账户
     * @param userPassword 密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param user 脱敏前的用户信息
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User user);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList 标签名称列表
     * @return 搜索到的用户
     */
    List<User> searchUsersByTagNames(List<String> tagNameList);

    /**
     * 用户修改
     *
     * @param user 用户信息
     * @param request
     * @return 是否修改成功
     */
    int updateUser(User user, HttpServletRequest request);
}
