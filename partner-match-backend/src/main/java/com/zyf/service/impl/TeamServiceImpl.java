package com.zyf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyf.model.domain.Team;
import com.zyf.service.TeamService;
import com.zyf.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author zyf
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




