package com.zyf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyf.model.domain.UserTeam;
import com.zyf.service.UserTeamService;
import com.zyf.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
 * 用户队伍关系服务
 *
* @author zyf
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




