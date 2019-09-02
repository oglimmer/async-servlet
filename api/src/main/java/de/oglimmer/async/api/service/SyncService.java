package de.oglimmer.async.api.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.oglimmer.async.api.component.FakeBackendUri;
import de.oglimmer.async.api.component.TimeStats;

@RestController
public class SyncService {

	@Autowired
	@Qualifier("sync")
	private TimeStats stats;

	@Autowired
	private FakeBackendUri uri;

	// move to RestTemplate
	private HttpClient client = HttpClient.newHttpClient();

	@GetMapping(value = "/sync")
	public String get() {
		String data = getDataFromBackend();
		return data;
	}

	private String getDataFromBackend() {
		try {
			long start = System.currentTimeMillis();
			HttpRequest req = HttpRequest.newBuilder(uri.get()).GET().build();
			HttpResponse<String> response = client.send(req, BodyHandlers.ofString());
			stats.onComplete(System.currentTimeMillis() - start);
			return response.body();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}