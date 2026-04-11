package com.example.MentalHealth_Backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "Mental Health API activa. WebSocket: /ws-notifications · POST notificaciones: /api/notifications/send";
	}
}
