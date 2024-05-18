package com.zyf.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zyf.common.BaseResponse;
import com.zyf.common.ErrorCode;
import com.zyf.common.ResultUtils;
import com.zyf.model.domain.User;
import com.zyf.model.request.UserFriendRemoveRequest;
import com.zyf.model.request.UserLoginRequest;
import com.zyf.model.request.UserRegisterRequest;
import com.zyf.exception.BusinessException;
import com.zyf.model.request.UserTagNameUpdateRequest;
import com.zyf.model.vo.UserFriendshipVO;
import com.zyf.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author zyf
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class UserController {

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册请求体
     * @return 新用户 id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String username = userRegisterRequest.getUsername();
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(username, userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(username, userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginRequest 用户登录请求体
     * @param request
     * @return 脱敏后的用户信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销接口
     *
     * @param request
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        int result = this.userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户登录态
     *
     * @param request
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User safetyUser = userService.getLoginUser(request);
        if (safetyUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return ResultUtils.success(safetyUser);
    }

    /**
     * 查询用户接口
     *
     * @param searchText 关键词
     * @return 用户集合
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(@RequestParam("searchText") String searchText, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(searchText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "关键词不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (loginUser != null) {
            queryWrapper.ne("id", loginUser.getId());
        }
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("username", searchText)
                    .or().like("user_profile", searchText).or().like("tag_names", searchText));
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> safetyUserList = userList.stream().map((user) -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(safetyUserList);
    }

    /**
     * 根据标签名称搜索用户接口
     *
     * @param tagNameList 标签名称集合
     * @return 搜索到的用户集合
     */
    @GetMapping("/search/tagNames")
    public BaseResponse<List<User>> searchUsersByTagNames(@RequestParam List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTagNames(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 推荐用户接口
     *
     * @param pageSize 页面大小
     * @param pageNum  页号
     * @param request
     * @return 推荐的用户
     */
    @GetMapping("/recommend")
    public BaseResponse<List<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<User> safetyUserList = userService.recommendUsers(pageSize, pageNum, loginUser);
        return ResultUtils.success(safetyUserList);
    }

    /**
     * 删除接口
     *
     * @param id 用户id
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        // 仅限管理员删除
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新用户接口
     *
     * @param user    用户信息
     * @param request
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        int updateResult = userService.updateUser(user, loginUser);
        return ResultUtils.success(updateResult);
    }

    /**
     * 修改用户标签接口
     *
     * @param userTagNameUpdateRequest 修改后的标签
     * @param request
     * @return 是否修改成功
     */
    @PostMapping("/update/tagNames")
    public BaseResponse<Boolean> updateTagNames(@RequestBody UserTagNameUpdateRequest userTagNameUpdateRequest, HttpServletRequest request) {
        if (userTagNameUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String tagNames = userTagNameUpdateRequest.getTagNames();
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", loginUser.getId());
        updateWrapper.set("tag_names", tagNames);
        boolean result = userService.update(updateWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 鉴权
     *
     * @param request
     * @return 是否为管理员
     */
    public boolean isAdmin(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return userService.isAdmin(loginUser);
    }

    /**
     * 根据标签匹配用户
     *
     * @param num 匹配的用户数量
     * @param request
     * @return 匹配到的用户
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num < 1 || num > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "匹配数量不符合要求");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录账号");
        }
        List<User> userList = userService.matchUsers(num, loginUser);
        return ResultUtils.success(userList);
    }

    /**
     * 更换用户头像接口
     *
     * @param avatarFile 头像文件
     * @param request
     * @return 是否更换成功
     */
    @PostMapping("/avatar")
    public BaseResponse<Boolean> updateUserAvatar(@RequestParam("avatarFile") MultipartFile avatarFile, HttpServletRequest request) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Boolean result = userService.updateUserAvatar(avatarFile, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更换头像失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户接口
     *
     * @param userId 用户 id
     * @return 脱敏后的用户信息
     */
    @GetMapping("/get/id")
    public BaseResponse<User> getUserById(@RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 根据用户 id 获取用户集合
     *
     * @param userIdList 用户 id 集合
     * @param friendshipIdList 好友申请 id 集合
     * @return 用户 + 好友申请 id 的集合
     */
    @GetMapping("/get/ids")
    public BaseResponse<List<UserFriendshipVO>> getUsersByIds(@RequestParam List<Long> userIdList, @RequestParam List<Long> friendshipIdList) {
        List<UserFriendshipVO> userFriendshipVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(userIdList)) {
            return ResultUtils.success(userFriendshipVOList);
        }
        Map<Long, Long> userIdFriendshipIdMap = new HashMap<>();
        for (int i = 0; i < userIdList.size(); i++) {
            userIdFriendshipIdMap.put(userIdList.get(i), friendshipIdList.get(i));
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", userIdList);
        List<User> userList = userService.list(queryWrapper);
        userFriendshipVOList = userList.stream().map(userService::getSafetyUser).map(user -> {
            UserFriendshipVO userFriendshipVO = new UserFriendshipVO();
            BeanUtils.copyProperties(user, userFriendshipVO);
            userFriendshipVO.setFriendshipId(userIdFriendshipIdMap.get(user.getId()));
            return userFriendshipVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userFriendshipVOList);
    }

    /**
     * 获取当前用户的好友
     *
     * @param request
     * @return 好友集合
     */
    @GetMapping("/get/friends")
    public BaseResponse<List<User>> getUserFriends(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        String friendIds = loginUser.getFriendIds();
        List<Long> friendIdList = new Gson().fromJson(friendIds, new TypeToken<List<Long>>() {
        }.getType());
        List<User> safetyList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(friendIdList)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", friendIdList);
            List<User> friendList = userService.list(queryWrapper);
            safetyList = friendList.stream().map(userService::getSafetyUser).collect(Collectors.toList());
        }
        return ResultUtils.success(safetyList);
    }

    /**
     * 删除用户的好友
     *
     * @param userFriendRemoveRequest 用户好友删除请求体
     * @param request
     * @return 是否删除成功
     */
    @PostMapping("/remove/friend")
    public BaseResponse<Boolean> removeUserFriend(@RequestBody UserFriendRemoveRequest userFriendRemoveRequest, HttpServletRequest request) {
        if (userFriendRemoveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long friendId = userFriendRemoveRequest.getFriendId();
        if (friendId == null || friendId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Boolean result = userService.removeUserFriend(loginUser, friendId);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }
}
