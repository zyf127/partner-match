package com.zyf.model.vo;

import com.zyf.model.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ChatMessageVO implements Serializable {

    private static final long serialVersionUID = -5515266734283060843L;

    /**
     * id
     */
    private Long id;

    /**
     * 发送消息的用户
     */
    private User fromUser;

    /**
     * 接收消息 id
     */
    private Long toId;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 消息类型 1 - 私聊 2 - 群聊
     */
    private Integer messageType;

    /**
     * 队伍 id
     */
    private Long teamId;

    /**
     * 创建时间
     */
    private Date createTime;
}
