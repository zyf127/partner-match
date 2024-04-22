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
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 关键词
     */
    private String searchText;
}