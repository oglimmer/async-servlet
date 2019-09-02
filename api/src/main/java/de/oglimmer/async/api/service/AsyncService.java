package de.oglimmer.async.api.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import de.oglimmer.async.api.component.TimeStats;
import reactor.core.publisher.Mono;

@RestController
public class AsyncService {

	@Autowired
	@Qualifier("async")
	private TimeStats stats;

	private WebClient client = WebClient.create("http://localhost:9090/queryResource");

	@GetMapping(value = "/async")
	public CompletableFuture<String> get() {
		final long start = System.currentTimeMillis();
		Mono<String> mono = client.get().retrieve().bodyToMono(String.class);
		return mono.toFuture().whenComplete((success, error) -> {
			stats.onComplete(System.currentTimeMillis() - start);
		});
	}

}