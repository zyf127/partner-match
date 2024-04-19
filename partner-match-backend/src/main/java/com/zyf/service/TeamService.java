package com.zyf.service;

import com.zyf.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyf.model.domain.User;
import com.zyf.model.dto.TeamQuery;
import com.zyf.model.request.TeamUpdateRequest;
import com.zyf.model.vo.TeamUserVO;

import java.util.List;

/**
* @author zyf
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     *
     * @param team 队伍信息
     * @param loginUser 登录的用户
     * @return 队伍id
     */
    long addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);
}
