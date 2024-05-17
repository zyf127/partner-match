package com.zyf.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友申请表
 * @TableName friendship
 */
@TableName(value ="friendship")
@Data
public class Friendship implements Serializable {

    /**
     * 好友申请 id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送申请的用户 id
     */
    private Long fromId;

    /**
     * 接收申请的用户 id 
     */
    private Long toId;

    /**
     * 申请状态 默认 0 （0-等待验证 1-已同意 2-已拒绝）
     */
    private Integer friendshipStatus;

    /**
     * 好友申请验证信息
     */
    private String friendshipMessage;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = -7450994062344273651L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Friendship other = (Friendship) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getFromId() == null ? other.getFromId() == null : this.getFromId().equals(other.getFromId()))
            && (this.getToId() == null ? other.getToId() == null : this.getToId().equals(other.getToId()))
            && (this.getFriendshipStatus() == null ? other.getFriendshipStatus() == null : this.getFriendshipStatus().equals(other.getFriendshipStatus()))
            && (this.getFriendshipMessage() == null ? other.getFriendshipMessage() == null : this.getFriendshipMessage().equals(other.getFriendshipMessage()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getFromId() == null) ? 0 : getFromId().hashCode());
        result = prime * result + ((getToId() == null) ? 0 : getToId().hashCode());
        result = prime * result + ((getFriendshipStatus() == null) ? 0 : getFriendshipStatus().hashCode());
        result = prime * result + ((getFriendshipMessage() == null) ? 0 : getFriendshipMessage().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", fromId=").append(fromId);
        sb.append(", toId=").append(toId);
        sb.append(", friendshipStatus=").append(friendshipStatus);
        sb.append(", friendshipMessage=").append(friendshipMessage);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}