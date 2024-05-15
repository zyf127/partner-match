package com.zyf.ws;

import com.google.gson.Gson;
import com.zyf.common.ErrorCode;
import com.zyf.exception.BusinessException;
import com.zyf.model.domain.ChatMessage;
import com.zyf.model.domain.User;
import com.zyf.model.request.ChatMessageRequest;
import com.zyf.model.vo.ChatMessageVO;
import com.zyf.service.ChatMessageService;
import com.zyf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.zyf.constant.UserConstant.USER_LOGIN_STATE;

@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfigurator.class)
@Component
@Slf4j
public class ChatMessageEndpoint {

    /**
     * 上线的用户
     */
    private static final Map<Long, Session> onlineUsers = new ConcurrentHashMap<>();

    /**
     * 当前登录的用户
     */
    private User loginUser;

    /**
     * 聊天消息服务
     */
    private static ChatMessageService chatMessageService;
    @Resource
    public void setChatMessageService(ChatMessageService chatMessageService) {
        ChatMessageEndpoint.chatMessageService = chatMessageService;
    }

    /**
     * 用户服务
     */
    private static UserService userService;
    @Resource
    public void setUserService(UserService userService) {
        ChatMessageEndpoint.userService = userService;
    }

    /**
     * 建立 WebSocket 连接时被调用
     *
     * @param session
     * @param config
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // 保存 session
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        loginUser = (User) httpSession.getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        onlineUsers.put(loginUser.getId(), session);
    }

    /**
     * 浏览器发送消息到服务端时被调用
     *
     * @param message 消息内容
     */

    @OnMessage
    public void onMessage(String message) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        ChatMessageRequest chatMessageRequest = new Gson().fromJson(message, ChatMessageRequest.class);
        ChatMessage chatMessage = new ChatMessage();
        BeanUtils.copyProperties(chatMessageRequest, chatMessage);
        chatMessage.setFromId(loginUser.getId());
        boolean saveResult = chatMessageService.save(chatMessage);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        ChatMessage totalChatMessage = chatMessageService.getById(chatMessage.getId());
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        chatMessageVO.setFromUser(userService.getSafetyUser(loginUser));
        BeanUtils.copyProperties(totalChatMessage, chatMessageVO);
        broadcastAllUsers(chatMessageVO);
    }

    private void broadcastAllUsers(ChatMessageVO chatMessageVO) {
        Set<Map.Entry<Long, Session>> entries = onlineUsers.entrySet();
        for (Map.Entry<Long, Session> entry : entries) {
            Session session = entry.getValue();
            try {
                session.getBasicRemote().sendText(new Gson().toJson(chatMessageVO));
            } catch (IOException e) {
                log.error("broadcast message message error", e);
            }
        }
    }

    /**
     * 断开 WebSocket 连接时被调用
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        onlineUsers.remove(loginUser.getId());
    }
}
