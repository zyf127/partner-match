package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 3361692849199914901L;

    /**
     * 队伍 id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}
