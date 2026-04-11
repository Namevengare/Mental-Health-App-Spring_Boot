package com.example.MentalHealth_Backend;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationHandler handler;

	public NotificationController(NotificationHandler handler) {
		this.handler = handler;
	}

	@PostMapping("/send")
	public String send(@RequestBody String message) {
		handler.sendPushNotification(message);
		return "Notificación enviada";
	}
}
