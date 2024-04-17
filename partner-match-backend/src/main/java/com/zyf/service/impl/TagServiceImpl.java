package com.zyf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyf.model.domain.Tag;
import com.zyf.service.TagService;
import com.zyf.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
 * 标签服务
 *
* @author zyf
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




