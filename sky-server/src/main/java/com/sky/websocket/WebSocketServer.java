package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 服务端
 *
 * @ServerEndpoint("/ws/{sid}") - 定义 WebSocket 端点
 * sid 是客户端标识（管理端、用户端等）
 */
@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    /**
     * 存放所有在线连接
     * key: sid（客户端标识）
     * value: WebSocketServer 实例
     */
    private static final Map<String, WebSocketServer> onlineSessions = new HashMap<>();

    /**
     * 当前连接的 session
     */
    private Session session;

    /**
     * 当前连接的 sid
     */
    private String sid;

    /**
     * 连接建立时调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        this.sid = sid;
        onlineSessions.put(sid, this);
        log.info("WebSocket 连接建立: sid={}, 当前在线数={}", sid, onlineSessions.size());
    }

    /**
     * 收到客户端消息时调用
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端消息: sid={}, message={}", sid, message);
    }

    /**
     * 连接关闭时调用
     */
    @OnClose
    public void onClose() {
        onlineSessions.remove(sid);
        log.info("WebSocket 连接关闭: sid={}, 当前在线数={}", sid, onlineSessions.size());
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误: sid={}", sid, error);
    }

    /**
     * 向指定客户端发送消息
     */
    public void sendToOne(String sid, String message) {
        WebSocketServer server = onlineSessions.get(sid);
        if (server != null && server.session != null) {
            try {
                server.session.getBasicRemote().sendText(message);
                log.info("发送消息给 {}: {}", sid, message);
            } catch (IOException e) {
                log.error("发送消息失败: sid={}", sid, e);
            }
        }
    }

    /**
     * 向所有客户端广播消息
     */
    public void sendToAll(String message) {
        Collection<WebSocketServer> servers = onlineSessions.values();
        for (WebSocketServer server : servers) {
            if (server.session != null) {
                try {
                    server.session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("广播消息失败: sid={}", server.sid, e);
                }
            }
        }
        log.info("广播消息给所有客户端: 当前在线数={}", servers.size());
    }

    /**
     * 获取静态实例（供外部调用）
     * 因为 Spring 默认是单例，但 @ServerEndpoint 会为每个连接创建新实例
     * 所以用静态 map 存储所有连接
     */
    public static WebSocketServer getInstance(String sid) {
        return onlineSessions.get(sid);
    }

    public static Collection<WebSocketServer> getAllInstances() {
        return onlineSessions.values();
    }
}
