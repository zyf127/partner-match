package com.zyf.model.enums;

/**
 * 聊天消息类型枚举
 */
public enum MessageTypeEnum {
    PUBLIC(0, "世界聊天"),
    TEAM(1, "队伍聊天"),
    PRIVATE(2, "私聊");
    private int value;
    private String text;

    MessageTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static int getValueByEnum(MessageTypeEnum messageTypeEnum) {
        return messageTypeEnum.getValue();
    }
}
