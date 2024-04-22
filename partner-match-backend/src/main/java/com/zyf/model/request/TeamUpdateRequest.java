package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 5833561635848665188L;

    /**
     * 队伍 id
     */
    private Long teamId;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String teamDescription;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队伍状态  0 - 公开，1 - 私有，2 - 加密
     */
    private Integer teamStatus;

    /**
     * 队伍密码
     */
    private String teamPassword;

}
