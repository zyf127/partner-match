package com.zyf.mapper;

import com.zyf.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zyf
*/
public interface UserMapper extends BaseMapper<User> {
    /**
     *  获取指定队伍中的用户
     * @param teamId 队伍id
     * @return 用户列表
     */
    List<User> selectUsersByTeamId(Long teamId);
}




