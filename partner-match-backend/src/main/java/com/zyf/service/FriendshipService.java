package com.zyf.service;

import com.zyf.model.domain.Friendship;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyf.model.domain.User;
import com.zyf.model.request.FriendshipSendRequest;

/**
* @author zyf
*/
public interface FriendshipService extends IService<Friendship> {

    /**
     * 发送好友申请
     *
     * @param friendshipSendRequest 好友申请发送请求体
     * @param loginUser 当前登录的用户
     * @return 是否发送成功
     */
    boolean sendFriendship(FriendshipSendRequest friendshipSendRequest, User loginUser);
}
