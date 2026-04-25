package com.example.MentalHealth_Backend;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PubMedNewsController {

	private final PubMedNewsService pubMedNewsService;

	public PubMedNewsController(PubMedNewsService pubMedNewsService) {
		this.pubMedNewsService = pubMedNewsService;
	}

	@GetMapping("/api/news/mental-health")
	public List<PubMedNewsItem> getMentalHealthNews(
			@RequestParam(defaultValue = "10") int limit,
			@RequestParam(required = false) String query) {
		int safeLimit = Math.max(1, Math.min(limit, 20));
		return pubMedNewsService.fetchMentalHealthNews(safeLimit, query);
	}
}
