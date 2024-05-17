package com.zyf.model.vo;

import lombok.Data;

@Data
public class UserFriendshipVO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个人简介
     */
    private String userProfile;

    /**
     * 标签名称 json 列表
     */
    private String tagNames;

    /**
     * 好友 id 列表
     */
    private String friendIds;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 好友申请 id
     */
    private Long friendshipId;
}
