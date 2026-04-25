package com.example.MentalHealth_Backend;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PubMedNewsService {

	private static final String BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils";
	private static final String MENTAL_HEALTH_FILTER =
			"(\"Mental Health\"[Mesh] OR mental health[Title/Abstract] OR "
					+ "depression[Title/Abstract] OR anxiety[Title/Abstract] OR "
					+ "suicide[Title/Abstract] OR \"substance-related disorders\"[Mesh] OR "
					+ "\"eating disorders\"[Mesh] OR \"stress, psychological\"[Mesh])";

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${ncbi.tool-name:MentalHealthBackend}")
	private String toolName;

	@Value("${ncbi.email:}")
	private String email;

	@Value("${ncbi.api-key:}")
	private String apiKey;

	public List<PubMedNewsItem> fetchMentalHealthNews(int limit, String customQuery) {
		try {
			String query = (customQuery == null || customQuery.isBlank())
					? MENTAL_HEALTH_FILTER
					: "(" + customQuery + ") AND " + MENTAL_HEALTH_FILTER;

			List<String> pmids = searchRecentPmids(query, limit);
			if (pmids.isEmpty()) {
				return List.of();
			}
			return loadSummaries(pmids);
		} catch (Exception e) {
			throw new IllegalStateException("No se pudieron obtener noticias desde PubMed", e);
		}
	}

	private List<String> searchRecentPmids(String query, int limit) throws IOException, InterruptedException {
		String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
		String url = BASE_URL + "/esearch.fcgi?db=pubmed&retmode=json&sort=pub+date&retmax=" + limit
				+ "&term=" + encodedQuery + buildCommonParams();

		JsonNode root = executeJsonGet(url);
		JsonNode idList = root.path("esearchresult").path("idlist");
		List<String> pmids = new ArrayList<>();
		for (JsonNode id : idList) {
			pmids.add(id.asText());
		}
		return pmids;
	}

	private List<PubMedNewsItem> loadSummaries(List<String> pmids) throws IOException, InterruptedException {
		String ids = String.join(",", pmids);
		String url = BASE_URL + "/esummary.fcgi?db=pubmed&retmode=json&id=" + ids + buildCommonParams();
		JsonNode result = executeJsonGet(url).path("result");

		List<PubMedNewsItem> items = new ArrayList<>();
		for (String pmid : pmids) {
			JsonNode doc = result.path(pmid);
			if (doc.isMissingNode()) {
				continue;
			}

			String title = doc.path("title").asText("");
			String journal = doc.path("fulljournalname").asText("");
			String pubDate = doc.path("pubdate").asText("");
			String doi = extractDoi(doc.path("articleids"));
			String pubmedUrl = "https://pubmed.ncbi.nlm.nih.gov/" + pmid + "/";

			items.add(new PubMedNewsItem(
					pmid,
					title,
					journal,
					pubDate,
					doi,
					pubmedUrl,
					Instant.now().toString()));
		}
		return items;
	}

	private String extractDoi(JsonNode articleIds) {
		if (articleIds == null || !articleIds.isArray()) {
			return "";
		}
		for (JsonNode idNode : articleIds) {
			if ("doi".equalsIgnoreCase(idNode.path("idtype").asText())) {
				return idNode.path("value").asText("");
			}
		}
		return "";
	}

	private JsonNode executeJsonGet(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Accept", "application/json")
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() >= 400) {
			throw new IllegalStateException("PubMed devolvio HTTP " + response.statusCode());
		}
		return objectMapper.readTree(response.body());
	}

	private String buildCommonParams() {
		StringBuilder sb = new StringBuilder();
		if (!toolName.isBlank()) {
			sb.append("&tool=").append(URLEncoder.encode(toolName, StandardCharsets.UTF_8));
		}
		if (!email.isBlank()) {
			sb.append("&email=").append(URLEncoder.encode(email, StandardCharsets.UTF_8));
		}
		if (!apiKey.isBlank()) {
			sb.append("&api_key=").append(URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
		}
		return sb.toString();
	}
}
