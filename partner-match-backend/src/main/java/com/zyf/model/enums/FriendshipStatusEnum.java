package com.zyf.model.enums;


import com.zyf.model.domain.Friendship;

public enum FriendshipStatusEnum {

    WAIT(0, "等待验证"),
    AGREE(1, "已同意"),
    REFUSE(2, "已拒绝");

    private int value;
    private String text;

    public static int getValueByEnum(FriendshipStatusEnum friendshipStatusEnum) {
        return friendshipStatusEnum.getValue();
    }

    FriendshipStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
