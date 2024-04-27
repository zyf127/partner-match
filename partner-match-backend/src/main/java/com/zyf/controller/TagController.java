package com.zyf.controller;

import com.zyf.common.BaseResponse;
import com.zyf.common.ResultUtils;
import com.zyf.model.domain.Tag;
import com.zyf.service.TagService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 标签接口
 *
 * @author zyf
 */
@RestController
@RequestMapping("/tag")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class TagController {
    /**
     * 标签服务
     */
    @Resource
    private TagService tagService;

    @GetMapping("/get")
    public BaseResponse<List<Tag>> getAllTags() {
        List<Tag> tagList = tagService.list();
        return ResultUtils.success(tagList);
    }
}
