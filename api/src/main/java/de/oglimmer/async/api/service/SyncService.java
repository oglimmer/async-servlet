package de.oglimmer.async.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import de.oglimmer.async.api.component.TimeStats;

@RestController
public class SyncService {

	@Autowired
	@Qualifier("sync")
	private TimeStats stats;

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping(value = "/sync")
	public String get() {
		String data = getDataFromBackend();
		return data;
	}

	private String getDataFromBackend() {
		long start = System.currentTimeMillis();
		String answer = restTemplate.getForObject("http://localhost:9090/queryResource", String.class);
		stats.onComplete(System.currentTimeMillis() - start);
		return answer;
	}

}