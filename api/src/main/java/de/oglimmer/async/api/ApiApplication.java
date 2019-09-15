package de.oglimmer.async.api;

import de.oglimmer.async.api.component.TimeStats;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Configuration
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public JettyServletWebServerFactory jettyEmbeddedServletContainerFactory() {
		JettyServletWebServerFactory jettyContainer = new JettyServletWebServerFactory();

		jettyContainer.setPort(8080);
		jettyContainer.setThreadPool(new QueuedThreadPool(50, 50));
		return jettyContainer;
	}

	@Bean("sync")
	public TimeStats getTimeStatsSync() {
		return new TimeStats("Sync");
	}

	@Bean("async")
	public TimeStats getTimeStatsAsync() {
		return new TimeStats("Async");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
