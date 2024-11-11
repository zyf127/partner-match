package com.zyf.ws;

import com.google.gson.Gson;
import com.zyf.common.ErrorCode;
import com.zyf.exception.BusinessException;
import com.zyf.model.domain.ChatMessage;
import com.zyf.model.domain.User;
import com.zyf.model.enums.MessageTypeEnum;
import com.zyf.model.request.ChatMessageRequest;
import com.zyf.model.vo.ChatMessageVO;
import com.zyf.service.ChatMessageService;
import com.zyf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.zyf.constant.UserConstant.USER_LOGIN_STATE;

@ServerEndpoint(value = "/chat/{userIdStr}/{teamIdStr}", configurator = GetHttpSessionConfigurator.class)
@Component
@Slf4j
public class ChatMessageEndpoint {

    /**
     * 上线用户的连接信息
     */
    private static final Map<String, ConcurrentHashMap<Long, Session>> onlineUsers = new HashMap<>();

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
     * 聊天消息类型
     */
    private int messageType = MessageTypeEnum.getValueByEnum(MessageTypeEnum.PUBLIC);

    /**
     * 用户连接信息的 key
     */
    private String key = "-1_-1_-1";

    /**
     * 用户 id
     */
    private Long userId = -1L;

    /**
     * 队伍 id
     */
    private Long teamId = -1L;

    /**
     * 建立 WebSocket 连接时被调用
     *
     * @param session
     * @param userIdStr 用户 id
     * @param teamIdStr 队伍 id
     * @param config
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userIdStr") String userIdStr, @PathParam(value = "teamIdStr") String teamIdStr, EndpointConfig config) {
        // 保存 session
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        loginUser = (User) httpSession.getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 取出 userId 和 teamId
        if (StringUtils.isAnyBlank(userIdStr, teamIdStr)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long loginUserId = loginUser.getId();
        userId = Long.parseLong(userIdStr);
        teamId = Long.parseLong(teamIdStr);
        if (userId == -1 && teamId == -1) {
            // 世界聊天
            key = "-1_-1_-1";
            messageType = MessageTypeEnum.getValueByEnum(MessageTypeEnum.PUBLIC);
        } else if (userId == -1 && teamId != -1){
            // 队伍聊天
            key = "-1_-1_" + teamId;
            messageType = MessageTypeEnum.getValueByEnum(MessageTypeEnum.TEAM);
        } else if (userId != -1 && teamId == -1) {
            // 私聊
            Long firstId = null;
            Long secondId = null;
            if (loginUserId <= userId) {
                firstId = loginUserId;
                secondId = userId;
            } else {
                firstId = userId;
                secondId = loginUserId;
            }
            key = firstId + "_" + secondId + "_-1";
            messageType = MessageTypeEnum.getValueByEnum(MessageTypeEnum.PRIVATE);
        }
        ConcurrentHashMap<Long, Session> concurrentHashMap = onlineUsers.get(key);
        if (concurrentHashMap == null) {
            concurrentHashMap = new ConcurrentHashMap<>();
        }
        concurrentHashMap.put(loginUserId, session);
        onlineUsers.put(key, concurrentHashMap);
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
        if (messageType == MessageTypeEnum.getValueByEnum(MessageTypeEnum.PUBLIC)) {
            chatMessage.setMessageType(MessageTypeEnum.getValueByEnum(MessageTypeEnum.PUBLIC));
        } else if (messageType == MessageTypeEnum.getValueByEnum(MessageTypeEnum.TEAM)) {
            chatMessage.setMessageType(MessageTypeEnum.getValueByEnum(MessageTypeEnum.TEAM));
            chatMessage.setTeamId(teamId);
        } else {
            chatMessage.setMessageType(MessageTypeEnum.getValueByEnum(MessageTypeEnum.PRIVATE));
            chatMessage.setToId(userId);
        }
        boolean saveResult = chatMessageService.save(chatMessage);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        ChatMessage totalChatMessage = chatMessageService.getById(chatMessage.getId());
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        loginUser = userService.getById(loginUser.getId());
        chatMessageVO.setFromUser(userService.getSafetyUser(loginUser));
        BeanUtils.copyProperties(totalChatMessage, chatMessageVO);
        broadcastToUsers(chatMessageVO);
    }

    private void broadcastToUsers(ChatMessageVO chatMessageVO) {
        ConcurrentHashMap<Long, Session> concurrentHashMap = onlineUsers.get(key);
        Set<Map.Entry<Long, Session>> entries = concurrentHashMap.entrySet();
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
        ConcurrentHashMap<Long, Session> concurrentHashMap = onlineUsers.get(key);
        concurrentHashMap.remove(loginUser.getId());
    }
}
