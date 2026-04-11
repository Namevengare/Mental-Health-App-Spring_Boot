package com.example.MentalHealth_Backend;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotificationHandler extends TextWebSocketHandler {

	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		System.out.println("Nueva conexión: " + session.getId());
		session.sendMessage(new TextMessage("¡Conectado al servidor de Salud Mental!"));
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("Mensaje recibido: " + message.getPayload());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		System.out.println("Conexión cerrada: " + session.getId());
	}

	public void sendPushNotification(String message) {
		for (WebSocketSession session : sessions) {
			try {
				if (session.isOpen()) {
					session.sendMessage(new TextMessage(message));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
