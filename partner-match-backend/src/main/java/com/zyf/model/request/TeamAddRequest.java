package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = 8612306571459986187L;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String teamDescription;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 队伍状态  0 - 公开，1 - 私有，2 - 加密
     */
    private Integer teamStatus;

    /**
     * 队伍密码
     */
    private String teamPassword;
}
