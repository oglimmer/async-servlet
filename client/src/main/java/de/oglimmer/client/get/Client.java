package de.oglimmer.client.get;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <ol>
 * <li>Build config from defaults/cli-params</li>
 * <li>Use threadpool to execute http get calls</li>
 * <li>Each thread connects and reads back the result then terminates</li>
 * </ol>
 */
public class Client {

	private ExecutorService exec;
	private Statistics statistics;
	private Config config;

	public static void main(String[] args) {
		new Client(args);
	}

	private Client(String[] args) {
		buildUrl(args);
		statistics = new Statistics(this);
		statistics.start();
		mainLoop();
		statistics.print();
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public ExecutorService getExecutorService() {
		return exec;
	}

	public Config getConfig() {
		return config;
	}

	private void buildUrl(String[] args) {
		if (args.length < 1) {
			System.out.println("usage: URL [total_req [number_threads]]");
			System.exit(1);
		}
		this.config = new Config();
		if (args.length >= 2) {
			config.setTotalRequestsToDo(Long.parseLong(args[1]));
		}
		if (args.length >= 3) {
			config.setMaxNumberThreads(Integer.parseInt(args[2]));
		}
		exec = Executors.newFixedThreadPool(config.getMaxNumberThreads());
		config.setUrl(args[0]);
		System.out.println(config);
	}

	private void mainLoop() {
		int requestsDone = 0;
		AtomicLong done = new AtomicLong();
		while (requestsDone++ < config.getTotalRequestsToDo()) {
			final int id = requestsDone;
			exec.execute(() -> {
				ClientRequestProcessor crp = new ClientRequestProcessor(this);
				try {
					// SLOW RAMP-UP
					if (done.get() < config.getMaxNumberThreads()) {
						try {
							Thread.sleep(id * 2);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
					// ramp-up end
					crp.connect(config.getUrl());
					done.incrementAndGet();
					crp.run();
				} catch (IOException e) {
					// e.printStackTrace();
				} finally {
					crp.close();
				}
			});
		}
		try {
			exec.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
