package de.oglimmer.async.fakebackend.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class SlowResourceService {

	private final ExecutorService service = Executors.newFixedThreadPool(50);

	@GetMapping(path = "/queryResource")
	public DeferredResult<ResponseEntity<String>> query() {
		DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
		service.submit(() -> {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			result.setResult(ResponseEntity.ok("<backend-data>"));
		});
		return result;
	}

}
