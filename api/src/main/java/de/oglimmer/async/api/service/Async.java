package de.oglimmer.async.api.service;

import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import de.oglimmer.async.api.component.ThreadStats;
import reactor.core.publisher.Mono;

@RestController
public class Async {

	private WebClient client = WebClient.create("http://localhost:9090/queryResource");

	@Autowired
	private ThreadStats threadStats;

	/**
	 * Access via client/get.sh http://localhost:8080/async
	 */
	@GetMapping(value = "/async")
	public CompletableFuture<String> get() {
		int id = threadStats.incActive();
		Mono<String> mono = client.get().retrieve().bodyToMono(String.class);
		return mono.toFuture().whenComplete((success, error) -> {
			threadStats.decActive(id, true);
		});
	}

	/**
	 * Access via client/post.sh http://localhost:8080/async
	 * 
	 * curl -X POST http://localhost:8080/async
	 */
	@PostMapping(value = "/async")
	public void post(HttpServletRequest request) {
		if (!request.isAsyncSupported()) {
			throw new RuntimeException("async not supported");
		}
	}
}
