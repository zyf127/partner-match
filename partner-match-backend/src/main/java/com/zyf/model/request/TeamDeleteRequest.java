package com.zyf.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamDeleteRequest implements Serializable {

    private static final long serialVersionUID = -8341787333745365771L;

    /**
     * 队伍 id
     */
    private Long teamId;
}
