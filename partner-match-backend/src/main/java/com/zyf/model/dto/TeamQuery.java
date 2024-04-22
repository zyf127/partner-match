package com.zyf.model.dto;

import com.zyf.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuery extends PageRequest implements Serializable {

    private static final long serialVersionUID = -9168870907576666106L;

    /**
     * id
     */
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
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 队伍状态  0 - 公开，1 - 私有，2 - 加密
     */
    private Integer teamStatus;

    /**
     * 关键词
     */
    private String searchText;
}
