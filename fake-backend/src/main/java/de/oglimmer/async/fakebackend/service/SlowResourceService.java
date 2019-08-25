package de.oglimmer.async.fakebackend.service;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlowResourceService {

	@GetMapping(path = "/queryResource")
	public String query() {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return "<backend-data>";
	}

}
