package com.zyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zyf.common.ErrorCode;
import com.zyf.exception.BusinessException;
import com.zyf.model.domain.Friendship;
import com.zyf.model.domain.User;
import com.zyf.model.enums.FriendshipStatusEnum;
import com.zyf.model.request.FriendshipSendRequest;
import com.zyf.service.FriendshipService;
import com.zyf.mapper.FriendshipMapper;
import com.zyf.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
* @author zyf
*/
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, Friendship>
    implements FriendshipService {

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    @Override
    public boolean sendFriendship(FriendshipSendRequest friendshipSendRequest, User loginUser) {
        // 1.校验
        Long toId = friendshipSendRequest.getToId();
        if (toId == null || toId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User toUser = userService.getById(toId);
        if (toUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对方不存在");
        }
        Long loginUserId = loginUser.getId();

        // 2. 不能添加自己为好友
        if (loginUserId.equals(toId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能添加自己为好友");
        }

        // 3. 不能向好友发送好友申请
        String friendIds = loginUser.getFriendIds();
        List<Long> friendIdList = new Gson().fromJson(friendIds, new TypeToken<List<Long>>() {
        }.getType());
        if (!CollectionUtils.isEmpty(friendIdList) && friendIdList.contains(toId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对方已经是好友");
        }

        // 4. 好友申请不能重复
        QueryWrapper<Friendship> friendshipQueryWrapper = new QueryWrapper<>();
        friendshipQueryWrapper.eq("from_id", loginUserId);
        friendshipQueryWrapper.eq("to_id", toId);
        friendshipQueryWrapper.eq("friendship_status", FriendshipStatusEnum.getValueByEnum(FriendshipStatusEnum.WAIT));
        int count = this.count(friendshipQueryWrapper);
        if (count >= 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友申请不能重复");
        }

        // 5. 对方已经发送好友申请
        friendshipQueryWrapper = new QueryWrapper<>();
        friendshipQueryWrapper.eq("from_id", toId);
        friendshipQueryWrapper.eq("to_id", loginUserId);
        friendshipQueryWrapper.eq("friendship_status", FriendshipStatusEnum.getValueByEnum(FriendshipStatusEnum.WAIT));
        count = this.count(friendshipQueryWrapper);
        if (count >= 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对方已经发送好友申请");
        }

        Friendship friendship = new Friendship();
        BeanUtils.copyProperties(friendshipSendRequest, friendship);
        friendship.setFromId(loginUser.getId());
        return this.save(friendship);
    }
}




