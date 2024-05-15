package com.zyf.controller;

import com.zyf.common.BaseResponse;
import com.zyf.common.ResultUtils;
import com.zyf.model.domain.ChatMessage;
import com.zyf.model.domain.User;
import com.zyf.model.vo.ChatMessageVO;
import com.zyf.service.ChatMessageService;
import com.zyf.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @GetMapping("/getAll")
    public BaseResponse<List<ChatMessageVO>> getAllChatMessage() {
        List<ChatMessage> chatMessageList = chatMessageService.list();
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
