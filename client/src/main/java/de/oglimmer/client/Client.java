package de.oglimmer.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Client {

	private static int MAX_NUMBER_THREADS = 500;
	private ExecutorService exec;

	private long totalRequestsToDo = 500;
	private String url;
	private Statistics statistics;

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

	private void buildUrl(String[] args) {
		if (args.length < 1 || !("sync".equals(args[0]) || "async".equals(args[0]))) {
			System.out.println("Start with parameter sync or async");
			System.exit(1);
		}
		if (args.length >= 2) {
			totalRequestsToDo = Long.parseLong(args[1]);
		}
		if (args.length >= 3) {
			MAX_NUMBER_THREADS = Integer.parseInt(args[2]);
		}
		exec = Executors.newFixedThreadPool(MAX_NUMBER_THREADS);
		url = "http://localhost:8080/" + args[0];
		System.out.println("Using " + url + " with " + MAX_NUMBER_THREADS + " thread, calling it " + totalRequestsToDo
				+ " times.");
	}

	private void mainLoop() {
		int requestsDone = 0;
		AtomicLong done = new AtomicLong();
		while (requestsDone++ < totalRequestsToDo) {
			final int id = requestsDone;
			exec.execute(() -> {
				ClientRequestProcessor crp = new ClientRequestProcessor(this);
				try {
					// SLOW RAMP-UP
					if (done.get() < MAX_NUMBER_THREADS) {
						try {
							Thread.sleep(id * 2);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
					// ramp-up end
					crp.connect(url);
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

	public long getTotalRequestsToDo() {
		return totalRequestsToDo;
	}

}
