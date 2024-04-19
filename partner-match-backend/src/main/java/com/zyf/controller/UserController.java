package com.zyf.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyf.common.BaseResponse;
import com.zyf.common.ErrorCode;
import com.zyf.common.ResultUtils;
import com.zyf.model.domain.User;
import com.zyf.model.request.UserLoginRequest;
import com.zyf.model.request.UserRegisterRequest;
import com.zyf.exception.BusinessException;
import com.zyf.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author zyf
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
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
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
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
        return ResultUtils.success(safetyUser);
    }

    /**
     * 查询用户接口
     *
     * @param username 用户名
     * @return 用户集合
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        // 仅限管理员查询
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
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
    public BaseResponse<List<User>> searchUsersByTagNames(@RequestParam(required = false) List<String> tagNameList) {
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
        int updateResult = userService.updateUser(user, loginUser);
        return ResultUtils.success(updateResult);
    }

    /**
     * 鉴权
     *
     * @param request
     * @return 是否为管理员
     */
    public boolean isAdmin(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return userService.isAdmin(loginUser);
    }
}
