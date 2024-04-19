package com.zyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyf.common.ErrorCode;
import com.zyf.exception.BusinessException;
import com.zyf.model.domain.Team;
import com.zyf.model.domain.User;
import com.zyf.model.domain.UserTeam;
import com.zyf.model.dto.TeamQuery;
import com.zyf.model.enums.TeamStatusEnum;
import com.zyf.model.request.TeamUpdateRequest;
import com.zyf.model.vo.TeamUserVO;
import com.zyf.service.TeamService;
import com.zyf.mapper.TeamMapper;
import com.zyf.service.UserService;
import com.zyf.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 队伍服务
 *
* @author zyf
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 用户队伍关系服务
     */
    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1. 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final Long userId = loginUser.getId();
        // 3. 校验信息
        // 队伍人数 >= 2 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 2 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        // 队伍标题 <= 20
        String teamName = team.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // status 是否公开（int）不传默认为 0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        // 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
            team.setPassword(password);
        }

        // 超时时间晚于当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间早于当前时间");
        }
        // 校验用户最多创建 5 个队伍
        // TODO 有 bug，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        int hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建 5 个队伍");
        }
        // 4. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        // 5. 插入用户  => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建用户队伍关系失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1. 组合条件查询
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Long id = teamQuery.getId();
        String teamName = teamQuery.getTeamName();
        String description = teamQuery.getDescription();
        Integer maxNum = teamQuery.getMaxNum();
        Long userId = teamQuery.getUserId();
        Integer status = teamQuery.getStatus();
        String searchText = teamQuery.getSearchText();
        if (id != null && id > 0) {
            queryWrapper.eq("id", id);
        }
        if (StringUtils.isNotBlank(teamName)) {
            queryWrapper.like("team_name", teamName);
        }
        if (StringUtils.isNotBlank(description)) {
            queryWrapper.like("description", description);
        }
        if (maxNum != null) {
            queryWrapper.eq("max_num", maxNum);
        }
        if (userId != null && userId > 0) {
            queryWrapper.eq("user_id", userId);
        }
        if (status != null && status > -1) {
            queryWrapper.eq("status", status);
        }
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("team_name", searchText).or().like("description", searchText));
        }
        // 查询过期时间晚于当前时间的队伍
        queryWrapper.and(qw -> qw.gt("expire_time", new Date()));
        List<Team> teamList = this.list(queryWrapper);

        // 2. 将查询结果放入集合中
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            List<User> userList = userService.getUsersByTeamId(team.getId());
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            teamUserVO.setUserList(userList);
            teamUserVOList.add(teamUserVO);
        }

        // 3. 返回集合
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        // 1. 条件校验
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException((ErrorCode.NULL_ERROR), "队伍不存在");
        }
        // 只有队伍的创建者和管理员可以更新队伍
        if (oldTeam.getUserId() != loginUser.getId() && userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Integer status = teamUpdateRequest.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        TeamStatusEnum oldStatusEnum = TeamStatusEnum.getEnumByValue(oldTeam.getStatus());
        String password = teamUpdateRequest.getPassword();
        // 加密队伍必须要设置密码
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (!TeamStatusEnum.SECRET.equals(oldStatusEnum)) {
                if (StringUtils.isBlank(password)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
                }
            } else {
                if (StringUtils.isBlank((password))) {
                    password = null;
                }
            }
        }
        String teamName = teamUpdateRequest.getTeamName();
        String description = teamUpdateRequest.getDescription();
        Date expireTime = teamUpdateRequest.getExpireTime();

        // 2. 设置更新条件
        UpdateWrapper<Team> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(teamName != "", "team_name", teamName);
        updateWrapper.set(description != "", "description", description);
        updateWrapper.set(expireTime != null, "expire_time", expireTime);
        updateWrapper.set( "status", status);
        updateWrapper.set(password != null, "password", password);
        updateWrapper.eq("id", id);
        return this.update(updateWrapper);
    }
}




