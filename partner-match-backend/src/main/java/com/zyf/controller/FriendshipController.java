package com.zyf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zyf.common.BaseResponse;
import com.zyf.common.ErrorCode;
import com.zyf.common.ResultUtils;
import com.zyf.exception.BusinessException;
import com.zyf.model.domain.Friendship;
import com.zyf.model.domain.User;
import com.zyf.model.enums.FriendshipStatusEnum;
import com.zyf.model.request.FriendshipSendRequest;
import com.zyf.model.request.FriendshipUpdateRequest;
import com.zyf.service.FriendshipService;
import com.zyf.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 好友申请接口
 */
@RestController
@RequestMapping("/friendship")
public class FriendshipController {

    /**
     * 好友申请服务
     */
    @Resource
    private FriendshipService friendshipService;

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;


    /**
     * 发送好友申请接口
     *
     * @param friendshipSendRequest 好友申请发送请求体
     * @param request
     *
     * @return 是否发送成功
     */
    @PostMapping("/send")
    public BaseResponse<Boolean> sendFriendship(@RequestBody FriendshipSendRequest friendshipSendRequest, HttpServletRequest request) {
        if (friendshipSendRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = friendshipService.sendFriendship(friendshipSendRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get/from")
    public BaseResponse<List<Friendship>> getFriendshipByFromId(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        QueryWrapper<Friendship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("from_id", loginUser.getId());
        List<Friendship> friendshipList = friendshipService.list(queryWrapper);
        return ResultUtils.success(friendshipList);
    }

    @GetMapping("/get/to")
    public BaseResponse<List<Friendship>> getFriendshipByToId(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        QueryWrapper<Friendship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_id", loginUser.getId());
        List<Friendship> friendshipList = friendshipService.list(queryWrapper);
        return ResultUtils.success(friendshipList);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateFriendship(@RequestBody FriendshipUpdateRequest friendshipUpdateRequest, HttpServletRequest request) {
        // 1. 校验
        if (friendshipUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 2. 判断好友申请状态是否是等待验证
        Integer newFriendshipStatus = friendshipUpdateRequest.getFriendshipStatus();
        Long id = friendshipUpdateRequest.getId();
        Friendship friendship = friendshipService.getById(id);
        int waitValue = FriendshipStatusEnum.getValueByEnum(FriendshipStatusEnum.WAIT);
        if (friendship.getFriendshipStatus() != waitValue) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 3. 判断接收好友申请的用户是否是当前用户
        if (!friendship.getToId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 4. 更新好友申请状态
        UpdateWrapper<Friendship> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("friendship_status", newFriendshipStatus);
        updateWrapper.eq("id", id);
        boolean result = friendshipService.update(updateWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        // 5. 如果好友申请状态是已同意，则向双方的好友列表中添加对方为好友
        int agreeValue = FriendshipStatusEnum.getValueByEnum(FriendshipStatusEnum.AGREE);
        if (newFriendshipStatus == agreeValue) {
            result = userService.makeFriends(loginUser, friendship.getFromId());
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
        return ResultUtils.success(true);
    }
}
