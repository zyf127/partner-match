package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = -2212745412072240857L;

    /**
     * 队伍 id
     */
    private Long teamId;
}
