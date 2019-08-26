package de.oglimmer.async.api.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.oglimmer.async.api.component.TimeStats;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

@RestController
public class SyncService {

	private static TimeStats stats = new TimeStats("SyncService");

	private HttpClient client = HttpClient.newHttpClient();

	@GetMapping(value = "/sync")
	public String get() {
		String data = getDataFromBackend();
		return data;
	}

	private String getDataFromBackend() {
		try {
			long start = System.currentTimeMillis();
			HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:9090/queryResource")).GET().build();
			HttpResponse<String> response = client.send(req, BodyHandlers.ofString());
			stats.onComplete(System.currentTimeMillis() - start);
			return response.body();
		} catch (IOException | URISyntaxException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}