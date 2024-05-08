package com.zyf.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 队伍
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String teamDescription;

    /**
     * 队伍头像
     */
    private String avatarUrl;

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
    private static final long serialVersionUID = -6012446195350607489L;

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
        Team other = (Team) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTeamName() == null ? other.getTeamName() == null : this.getTeamName().equals(other.getTeamName()))
            && (this.getTeamDescription() == null ? other.getTeamDescription() == null : this.getTeamDescription().equals(other.getTeamDescription()))
            && (this.getMaxNum() == null ? other.getMaxNum() == null : this.getMaxNum().equals(other.getMaxNum()))
            && (this.getExpireTime() == null ? other.getExpireTime() == null : this.getExpireTime().equals(other.getExpireTime()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTeamStatus() == null ? other.getTeamStatus() == null : this.getTeamStatus().equals(other.getTeamStatus()))
            && (this.getTeamPassword() == null ? other.getTeamPassword() == null : this.getTeamPassword().equals(other.getTeamPassword()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTeamName() == null) ? 0 : getTeamName().hashCode());
        result = prime * result + ((getTeamDescription() == null) ? 0 : getTeamDescription().hashCode());
        result = prime * result + ((getMaxNum() == null) ? 0 : getMaxNum().hashCode());
        result = prime * result + ((getExpireTime() == null) ? 0 : getExpireTime().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTeamStatus() == null) ? 0 : getTeamStatus().hashCode());
        result = prime * result + ((getTeamPassword() == null) ? 0 : getTeamPassword().hashCode());
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
        sb.append(", teamName=").append(teamName);
        sb.append(", teamDescription=").append(teamDescription);
        sb.append(", maxNum=").append(maxNum);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", userId=").append(userId);
        sb.append(", teamStatus=").append(teamStatus);
        sb.append(", teamPassword=").append(teamPassword);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}