package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class FriendshipUpdateRequest implements Serializable {

    private static final long serialVersionUID = -3783814776664539839L;

    /**
     * 好友申请 id
     */
    private Long id;

    /**
     * 申请状态 默认 0 （0-等待验证 1-已同意 2-已拒绝）
     */
    private Integer friendshipStatus;
}
