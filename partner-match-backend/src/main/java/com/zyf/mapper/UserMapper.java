package com.zyf.mapper;

import com.zyf.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zyf
*/
public interface UserMapper extends BaseMapper<User> {
    /**
     *  获取指定队伍中的用户
     * @param teamId 队伍 id
     * @return 用户列表
     */
    List<User> selectUsersByTeamId(Long teamId);

    /**
     * 随机获取指定数量的用户
     *
     * @param id 当前登录用户 id
     * @param size 数量
     * @return 用户列表
     */
    List<User> selectRandomUserList(@Param("id") Long id, @Param("size") Long size);
}




