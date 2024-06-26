package com.zyf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyf.model.domain.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author zyf
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param username 昵称
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 检验密码
     * @return 新用户 id
     */
    long userRegister(String username, String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 账户
     * @param userPassword 密码
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
     * 获取当前用户登录态
     *
     * @param request
     * @return 当前用户信息
     */
    User getLoginUser(HttpServletRequest request);

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
     * 更新用户
     *
     * @param user      用户信息
     * @param loginUser
     * @return 是否更新成功
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取随机的用户列表
     *
     * @param id 登录用户的 id
     * @param size 用户列表的大小
     * @return 用户列表
     */
    List<User> getRandomUserList(Long id, Long size);

    /**
     * 推荐用户
     *
     * @param pageSize 页面大小
     * @param pageNum 页号
     * @param loginUser 当前登录的用户
     * @return 推荐的用户
     */
    List<User> recommendUsers(long pageSize, long pageNum, User loginUser);

    /**
     * 获取指定队伍中的用户
     *
     * @param teamId 队伍id
     * @return 用户列表
     */
    List<User> getUsersByTeamId(Long teamId);

    /**
     * 当前登录的用户是否为管理员
     *
     * @param loginUser 当前登录的用户
     * @return 是否为管理员
     */
    boolean isAdmin(User loginUser);

    /**
     * 根据标签匹配用户
     *
     * @param num 匹配的用户数量
     * @param loginUser 当前登录的用户
     * @return 匹配到的用户
     */
    List<User> matchUsers(long num, User loginUser);

    /**
     * 更换用户头像
     *
     * @param avatarFile 头像文件
     * @param loginUser 当前登录的用户
     * @return 是否更换成功
     */
    Boolean updateUserAvatar(MultipartFile avatarFile, User loginUser);

    /**
     * 添加好友
     *
     * @param loginUser 当前登录的用户
     * @param friendId 好友 id
     * @return 是否添加成功
     */
    Boolean makeFriends(User loginUser, Long friendId);

    /**
     * 删除用户的好友
     *
     * @param loginUser 当前登录的用户
     * @param friendId 好友 id
     * @return 是否删除成功
     */
    Boolean removeUserFriend(User loginUser, Long friendId);
}
