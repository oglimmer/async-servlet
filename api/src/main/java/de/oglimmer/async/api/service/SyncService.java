package de.oglimmer.async.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import de.oglimmer.async.api.component.ThreadStats;
import de.oglimmer.async.api.component.TimeStats;

@RestController
public class SyncService {

	@Autowired
	@Qualifier("sync")
	private TimeStats timeStats;
	
	@Autowired
	private ThreadStats threadStats;

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping(value = "/sync")
	public String get() {
		return getDataFromBackend();
	}

	private String getDataFromBackend() {
		long start = System.currentTimeMillis();
		String answer = restTemplate.getForObject("http://localhost:9090/queryResource", String.class);
		timeStats.onComplete(System.currentTimeMillis() - start);
		return answer;
	}


	@PostMapping(value = "/sync")
	public String post(@RequestParam("foo") String foo) {
		long start = System.currentTimeMillis();
		int id = threadStats.incActive();
		long totalBytes = 0;
		try {
			if (!("oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
					+ "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
					+ "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
					+ "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
					+ "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
					+ "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
					+ "oooooooooooooooooooooooooooooooooooooooooooooooooooooo").equals(foo)) {
				System.out.println("failed to get param: " + foo);
			}
			totalBytes = foo != null ? foo.length() : 0;
		} finally {
			threadStats.decActive(id);
			threadStats.incAll(System.currentTimeMillis() - start, totalBytes);
		}
		return "done";
	}
	
}