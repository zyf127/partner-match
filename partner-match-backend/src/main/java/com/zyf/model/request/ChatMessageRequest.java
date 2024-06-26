package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 聊天消息请求体
 */
@Data
public class ChatMessageRequest implements Serializable {


    private static final long serialVersionUID = 5923492656354685402L;

    /**
     * 接收消息 id
     */
    private Long toId;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 消息类型 0 - 世界聊天 1 - 队伍聊天 2 - 私聊
     */
    private Integer messageType;

    /**
     * 队伍 id
     */
    private Long teamId;

}
