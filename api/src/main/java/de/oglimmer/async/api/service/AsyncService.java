package de.oglimmer.async.api.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.Executors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import de.oglimmer.async.api.component.TimeStats;

@RestController
public class AsyncService {

	private static TimeStats stats = new TimeStats("AsyncService");
	
	private HttpClient client = HttpClient.newBuilder().executor(Executors.newFixedThreadPool(20)).build();

	private static URI uri;
	static {
		try {
			uri = new URI("http://localhost:9090/queryResource");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@GetMapping(value = "/async")
	public DeferredResult<ResponseEntity<String>> get() {
		final long start = System.currentTimeMillis();
		DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
		HttpRequest backendReq = HttpRequest.newBuilder(uri).GET().build();
		client.sendAsync(backendReq, BodyHandlers.ofString()).thenAccept((HttpResponse<String> response) -> {
			String data = response.body();
			stats.onComplete(System.currentTimeMillis() - start);
			result.setResult(ResponseEntity.ok(data));
		});
		return result;
	}

}