package com.zyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyf.common.ErrorCode;
import com.zyf.exception.BusinessException;
import com.zyf.model.domain.Team;
import com.zyf.model.domain.User;
import com.zyf.model.domain.UserTeam;
import com.zyf.model.dto.TeamQuery;
import com.zyf.model.enums.TeamStatusEnum;
import com.zyf.model.request.TeamDeleteRequest;
import com.zyf.model.request.TeamJoinRequest;
import com.zyf.model.request.TeamQuitRequest;
import com.zyf.model.request.TeamUpdateRequest;
import com.zyf.model.vo.TeamUserVO;
import com.zyf.service.TeamService;
import com.zyf.mapper.TeamMapper;
import com.zyf.service.UserService;
import com.zyf.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    /**
     * Redis 客户端
     */
    @Resource
    private RedissonClient redissonClient;

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
        // 队伍标题不为空且字数 <= 20
        String teamName = team.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // 描述 <= 512
        String teamDescription = team.getTeamDescription();
        if (StringUtils.isNotBlank(teamDescription) && teamDescription.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // teamStatus 是否公开（int）不传默认为 0（公开）
        int teamStatus = Optional.ofNullable(team.getTeamStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        // 如果 teamStatus 是加密状态，一定要有密码，且密码 <= 32
        String teamPassword = team.getTeamPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(teamPassword) || teamPassword.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
            team.setTeamPassword(teamPassword);
        }

        // 过期时间必须晚于当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间早于当前时间");
        }
        // 校验用户最多创建 5 个队伍
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
        String description = teamQuery.getTeamDescription();
        Integer maxNum = teamQuery.getMaxNum();
        Long userId = teamQuery.getUserId();
        Integer status = teamQuery.getTeamStatus();
        String searchText = teamQuery.getSearchText();
        if (id != null && id > 0) {
            queryWrapper.eq("id", id);
        }
        if (StringUtils.isNotBlank(teamName)) {
            queryWrapper.like("team_name", teamName);
        }
        if (StringUtils.isNotBlank(description)) {
            queryWrapper.like("team_description", description);
        }
        if (maxNum != null) {
            queryWrapper.eq("max_num", maxNum);
        }
        if (userId != null && userId > 0) {
            queryWrapper.eq("user_id", userId);
        }
        if (status != null && status > -1) {
            queryWrapper.eq("team_status", status);
        }
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("team_name", searchText).or().like("team_description", searchText));
        }
        // 查询过期时间晚于当前时间的队伍
        queryWrapper.and(qw -> qw.gt("expire_time", new Date()));

        List<Team> teamList = null;
        // 2. 判断是否需要分页
        Integer pageNum = teamQuery.getPageNum();
        Integer pageSize = teamQuery.getPageSize();
        if (pageNum != null && pageSize != null) {
            Page<Team> page = new Page<>(pageNum, pageSize);
            Page<Team> teamPage = this.page(page, queryWrapper);
            teamList = teamPage.getRecords();
        } else {
            teamList = this.list(queryWrapper);
        }

        // 3. 将查询结果放入集合中
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            List<User> userList = userService.getUsersByTeamId(team.getId());
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            teamUserVO.setUserList(userList);
            teamUserVOList.add(teamUserVO);
        }

        // 4. 返回集合
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        // 1. 条件校验
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getTeamId();
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
        Integer status = teamUpdateRequest.getTeamStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        TeamStatusEnum oldStatusEnum = TeamStatusEnum.getEnumByValue(oldTeam.getTeamStatus());
        String password = teamUpdateRequest.getTeamPassword();
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
        String description = teamUpdateRequest.getTeamDescription();
        Date expireTime = teamUpdateRequest.getExpireTime();

        // 2. 设置更新条件
        UpdateWrapper<Team> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(teamName != "", "team_name", teamName);
        updateWrapper.set(description != "", "team_description", description);
        updateWrapper.set(expireTime != null, "expire_time", expireTime);
        updateWrapper.set( "team_status", status);
        updateWrapper.set(password != null, "team_password", password);
        updateWrapper.eq("id", id);
        return this.update(updateWrapper);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1. 队伍必须存在
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }

        RLock rLock = redissonClient.getLock("partner-match:team:joinTeam:lock");
        try {
            int count = 0;
            while (count++ < 10) {
                // 只有一个线程能获取到锁
                if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    System.out.println("getLock: " + Thread.currentThread().getId());
                    // 2. 不能加入自己的队伍，不能重复加入已加入的队伍（幂等性）
                    QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_id", loginUser.getId());
                    queryWrapper.eq("team_id", teamId);
                    int userHasJoin = userTeamService.count(queryWrapper);
                    if (userHasJoin >= 1) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
                    }

                    // 3. 禁止加入私有的队伍
                    TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(team.getTeamStatus());
                    if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有的队伍");
                    }

                    // 4. 如果加入的队伍是加密的，必须密码匹配才可以
                    if (TeamStatusEnum.SECRET.equals(statusEnum) && (!team.getTeamPassword().equals(teamJoinRequest.getTeamPassword()))) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不正确");
                    }

                    // 5.只能加入未过期、未满的队伍
                    if (new Date().after(team.getExpireTime())) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
                    }
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("team_id", teamId);
                    int teamHasJoinNum = userTeamService.count(queryWrapper);
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                    }

                    // 6. 用户最多加入 5 个队伍
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_id", loginUser.getId());
                    int userHasJoinNum = userTeamService.count(queryWrapper);
                    if (userHasJoinNum >= 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多加入 5 个队伍");
                    }

                    // 7. 新增队伍 - 用户关联信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(loginUser.getId());
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUsers error", e);
            return false;
        } finally {
            // 只能释放自己的锁
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                System.out.println("unlock: " + Thread.currentThread().getId());
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        // 1. 校验请求参数
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 校验队伍是否存在
        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }

        // 3. 校验我是否已加入队伍
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", teamId);
        userTeamQueryWrapper.eq("user_id", userId);
        int userHasJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (userHasJoinTeam <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未加入该队伍");
        }

        // 4. 分析队伍的实际情况
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", teamId);
        int teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        // 判断队伍人数
        if (teamHasJoinNum == 1) {
            // 如果队伍只剩一人，解散队伍
            this.removeById(teamId);
        } else {
            // 如果队伍剩下至少两人，判断当前登录的用户是不是队长
            if (team.getUserId() == userId) {
                // 如果是队长退出队伍，权限转移给第二早加入的用户
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("team_id", teamId);
                userTeamQueryWrapper.last("ORDER BY id ASC LIMIT 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Team newTeam = new Team();
                newTeam.setId(teamId);
                newTeam.setUserId(nextUserTeam.getUserId());
                boolean result = this.updateById(newTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }

        // 5. 移除用户与队伍的关系
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", userId);
        userTeamQueryWrapper.eq("team_id", teamId);
        return userTeamService.remove(userTeamQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser) {
        // 1. 请求参数是否为空
        if (teamDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 队伍是否存在
        Long teamId = teamDeleteRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }

        // 3. 当前用户是否为队长
        if (team.getUserId() != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "没有解散队伍的权限");
        }

        // 4. 移除用户队伍关系
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId);
        boolean result = userTeamService.remove(queryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户队伍关联信息失败");
        }

        // 5. 删除队伍
        return this.removeById(teamId);
    }

    @Override
    public TeamUserVO getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        List<User> userList = userService.getUsersByTeamId(teamId);
        TeamUserVO teamUserVO = new TeamUserVO();
        BeanUtils.copyProperties(team, teamUserVO);
        teamUserVO.setUserList(userList);
        return teamUserVO;
    }
}




