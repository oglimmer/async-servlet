package de.oglimmer.async.api.service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import de.oglimmer.async.api.component.FakeBackendUri;
import de.oglimmer.async.api.component.TimeStats;

@RestController
public class AsyncService {

	@Autowired
	@Qualifier("async")
	private TimeStats stats;

	@Autowired
	private FakeBackendUri uri;

	private HttpClient client = HttpClient.newBuilder().executor(Executors.newFixedThreadPool(20)).build();

	@GetMapping(value = "/async")
	public DeferredResult<ResponseEntity<String>> get() {
		final long start = System.currentTimeMillis();
		DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
		HttpRequest backendReq = HttpRequest.newBuilder(uri.get()).GET().build();
		client.sendAsync(backendReq, BodyHandlers.ofString()).thenAccept((HttpResponse<String> response) -> {
			String data = response.body();
			stats.onComplete(System.currentTimeMillis() - start);
			result.setResult(ResponseEntity.ok(data));
		});
		return result;
	}

}