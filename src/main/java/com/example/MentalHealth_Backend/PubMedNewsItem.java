package com.example.MentalHealth_Backend;

public record PubMedNewsItem(
		String pmid,
		String title,
		String journal,
		String publicationDate,
		String doi,
		String pubmedUrl,
		String fetchedAt) {
}
