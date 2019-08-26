package de.oglimmer.async.fakebackend.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class SlowResourceService {

	private final ScheduledExecutorService service = Executors.newScheduledThreadPool(5);

	@GetMapping(path = "/queryResource")
	public DeferredResult<ResponseEntity<String>> query() {
		DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
		service.schedule(() -> {
			result.setResult(ResponseEntity.ok("<backend-data>"));
		}, 5, TimeUnit.SECONDS);
		return result;
	}

}
