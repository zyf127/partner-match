package com.zyf.ws;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 在 WebSocket 握手时获取 HttpSession
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取 HttpSession
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        // 保存 HttpSession
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
