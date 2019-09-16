package de.oglimmer.async.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Sync {

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Access via client/get.sh http://localhost:8080/sync
	 */
	@GetMapping(value = "/sync")
	public String get() {
		return restTemplate.getForObject("http://localhost:9090/queryResource", String.class);
	}

	/**
	 * Access via client/post.sh http://localhost:8080/sync
	 */
	@PostMapping(value = "/sync")
	public String post(@RequestParam("foo") String foo) {
		if ("bar".equals(foo)) {
			return "done";
		}
		System.out.println(foo);
		return "error";
	}

}