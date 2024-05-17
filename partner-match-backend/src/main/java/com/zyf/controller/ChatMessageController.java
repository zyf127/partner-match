package com.zyf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyf.common.BaseResponse;
import com.zyf.common.ErrorCode;
import com.zyf.common.ResultUtils;
import com.zyf.exception.BusinessException;
import com.zyf.model.domain.ChatMessage;
import com.zyf.model.domain.User;
import com.zyf.model.vo.ChatMessageVO;
import com.zyf.service.ChatMessageService;
import com.zyf.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天消息接口
 */
@RestController
@RequestMapping("/chatMessage")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class ChatMessageController {

    /**
     * 聊天消息服务
     */
    @Resource
    private ChatMessageService chatMessageService;

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 获取世界聊天的消息
     *
     * @return 世界聊天的消息
     */
    @GetMapping("/get/public")
    public BaseResponse<List<ChatMessageVO>> getPublicChatMessage() {
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("to_id");
        queryWrapper.isNull("team_id");
        List<ChatMessage> chatMessageList = chatMessageService.list(queryWrapper);
        List<ChatMessageVO> chatMessageVOList = chatMessageList.stream().map((chatMessage -> {
            User user = userService.getById(chatMessage.getFromId());
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            chatMessageVO.setFromUser(user);
            BeanUtils.copyProperties(chatMessage, chatMessageVO);
            return chatMessageVO;
        })).collect(Collectors.toList());
        return ResultUtils.success(chatMessageVOList);
    }

    /**
     * 获取队伍聊天的消息
     *
     * @return 队伍聊天的消息
     */
    @GetMapping("/get/team")
    public BaseResponse<List<ChatMessageVO>> getTeamChatMessage(@RequestParam Long teamId) {
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId);
        List<ChatMessage> chatMessageList = chatMessageService.list(queryWrapper);
        List<ChatMessageVO> chatMessageVOList = chatMessageList.stream().map((chatMessage -> {
            User user = userService.getById(chatMessage.getFromId());
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            chatMessageVO.setFromUser(user);
            BeanUtils.copyProperties(chatMessage, chatMessageVO);
            return chatMessageVO;
        })).collect(Collectors.toList());
        return ResultUtils.success(chatMessageVOList);
    }

    /**
     * 获取私聊的消息
     *
     * @return 私聊的消息
     */
    @GetMapping("/get/private")
    public BaseResponse<List<ChatMessageVO>> getTeamChatMessage(@RequestParam Long friendId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<Long> userIdList = Arrays.asList(loginUser.getId(), friendId);
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.in("from_id", userIdList).in("to_id", userIdList));
        List<ChatMessage> chatMessageList = chatMessageService.list(queryWrapper);
        List<ChatMessageVO> chatMessageVOList = chatMessageList.stream().map((chatMessage -> {
            User user = userService.getById(chatMessage.getFromId());
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            chatMessageVO.setFromUser(user);
            BeanUtils.copyProperties(chatMessage, chatMessageVO);
            return chatMessageVO;
        })).collect(Collectors.toList());
        return ResultUtils.success(chatMessageVOList);
    }


}
