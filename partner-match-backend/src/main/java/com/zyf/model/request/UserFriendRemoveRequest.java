package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserFriendRemoveRequest implements Serializable {
    private static final long serialVersionUID = 3318900029782323894L;

    private Long friendId;
}
