package com.zyf.service;

import com.zyf.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyf.model.domain.User;
import com.zyf.model.dto.TeamQuery;
import com.zyf.model.request.TeamDeleteRequest;
import com.zyf.model.request.TeamJoinRequest;
import com.zyf.model.request.TeamQuitRequest;
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

    /**
     * 查询队伍
     *
     * @param teamQuery 查询的信息
     * @return 匹配的队伍
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest 新的队伍信息
     * @param loginUser 当前登录的用户
     * @return 是否更新成功
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest 队伍信息
     * @param loginUser
     * @return 是否加入成功
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest 队伍信息
     * @param loginUser 当前登录的用户
     * @return 是否退出成功
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（退出）队伍
     * @param teamDeleteRequest 队伍信息
     * @param loginUser 当前登录的用户
     * @return 是否删除成功
     */
    boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser);

    /**
     * 根据 id 查询队伍
     *
     * @param teamId 队伍 id
     * @return 队伍信息
     */
    TeamUserVO getTeamById(Long teamId);
}
