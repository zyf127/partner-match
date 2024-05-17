package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class FriendshipSendRequest implements Serializable {

    private static final long serialVersionUID = -4181263475912462175L;

    /**
     * 接收申请的用户 id
     */
    private Long toId;

    /**
     * 好友申请验证信息
     */
    private String friendshipMessage;
}
