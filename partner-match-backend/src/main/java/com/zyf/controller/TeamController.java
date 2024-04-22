package com.zyf.controller;

import com.zyf.common.BaseResponse;
import com.zyf.common.ErrorCode;
import com.zyf.common.ResultUtils;
import com.zyf.model.domain.User;
import com.zyf.model.dto.TeamQuery;
import com.zyf.model.domain.Team;
import com.zyf.exception.BusinessException;
import com.zyf.model.request.*;
import com.zyf.model.vo.TeamUserVO;
import com.zyf.service.TeamService;
import com.zyf.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 队伍接口
 *
 * @author zyf
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class TeamController {
    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 队伍服务
     */
    @Resource
    private TeamService teamService;

    /**
     * 创建队伍接口
     *
     * @param teamAddRequest 队伍信息
     * @param request
     * @return 队伍id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        User loginUser = userService.getLoginUser(request);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 更新队伍接口
     *
     * @param teamUpdateRequest 新的队伍信息
     * @param request
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 查询队伍接口
     *
     * @param teamQuery 查询信息
     * @return 匹配的队伍
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        teamQuery.setPageNum(null);
        teamQuery.setPageSize(null);
        List<TeamUserVO> teamUserVOList = teamService.listTeams(teamQuery);
        return ResultUtils.success(teamUserVOList);
    }

    /**
     * 分页查询队伍接口
     *
     * @param teamQuery 查询信息
     * @return 匹配的队伍
     */
    @GetMapping("/list/page")
    public BaseResponse<List<TeamUserVO>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<TeamUserVO> teamUserVOList = teamService.listTeams(teamQuery);
        return ResultUtils.success(teamUserVOList);
    }

    /**
     * 查看指定队伍
     *
     * @param teamId 队伍 id
     * @return 指定队伍信息
     */
    @GetMapping("/detail")
    public BaseResponse<TeamUserVO> showTeamDetail(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        TeamUserVO teamUserVO = teamService.showTeamDetail(teamId);
        return ResultUtils.success(teamUserVO);
    }

    /**
     * 加入队伍接口
     *
     * @param teamJoinRequest 队伍信息
     * @return 是否加入成功
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     *  退出队伍接口
     * @param teamQuitRequest 队伍信息
     * @param request
     * @return 是否退出成功
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 删除（解散）队伍接口
     *
     * @param teamDeleteRequest 队伍信息
     * @param request
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request) {
        if (teamDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(teamDeleteRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍失败");
        }
        return ResultUtils.success(true);
    }
}

