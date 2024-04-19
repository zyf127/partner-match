package com.zyf.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyf.common.BaseResponse;
import com.zyf.common.ErrorCode;
import com.zyf.common.ResultUtils;
import com.zyf.model.domain.User;
import com.zyf.model.dto.TeamQuery;
import com.zyf.model.domain.Team;
import com.zyf.exception.BusinessException;
import com.zyf.model.request.TeamAddRequest;
import com.zyf.model.request.TeamUpdateRequest;
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
        User loginUser = userService.getCurrentUser(request);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 删除队伍接口
     *
     * @param id 队伍id
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
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
        User loginUser = userService.getCurrentUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败");
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
    public BaseResponse<List<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(page, queryWrapper);
        List<Team> teamList = teamPage.getRecords();
        return ResultUtils.success(teamList);
    }
}

