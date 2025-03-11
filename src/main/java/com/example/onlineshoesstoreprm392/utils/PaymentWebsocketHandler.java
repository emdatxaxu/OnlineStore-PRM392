package com.example.onlineshoesstoreprm392.utils;

import com.example.onlineshoesstoreprm392.security.JwtTokenProvider;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentWebsocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = getUsernameFromSession(session);
        userSessions.put(username, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userSessions.values().remove(session);
    }

    public static void notifyPaymentSuccess(String username) throws IOException{
        WebSocketSession session = userSessions.get(username);

        if(session != null && session.isOpen()){
            session.sendMessage(new TextMessage("payment_success"));
        }
    }

    private String getUsernameFromSession(WebSocketSession session){
        //vi du ws://server/ws/payment?token=123
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("username=")) {
            return query.split("=")[1]; // Lấy username từ URL
        }
        return null;
    }
}
