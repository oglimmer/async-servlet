package de.oglimmer.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Client {

	private static final int MAX_NUMBER_THREADS = 500;
	static final ExecutorService exec = Executors.newFixedThreadPool(MAX_NUMBER_THREADS);

	static long totalRequestsToDo = 50000000;
	static String url;

	static AtomicLong finishedCalls = new AtomicLong();
	static AtomicLong totalTimeSpent = new AtomicLong();
	static AtomicLong totalFailedRequests = new AtomicLong();
	static Thread statsThread;

	public static void main(String[] args) throws InterruptedException {
		buildUrl(args);
		statsThread();
		mainLoop();
		System.out.println("Processing took " + (totalTimeSpent.get() / (double) finishedCalls.get())
				+ " msec in average and it failed for " + totalFailedRequests.get());
	}

	private static void buildUrl(String[] args) {
		if (args.length < 1 || !("sync".equals(args[0]) || "async".equals(args[0]))) {
			System.out.println("Start with parameter sync or async");
			System.exit(1);
		}
		if (args.length == 2) {
			totalRequestsToDo = Long.parseLong(args[1]);
		}
		url = "http://localhost:8080/" + args[0];
		System.out.println("Using " + url + " with " + MAX_NUMBER_THREADS + " thread, calling it " + totalRequestsToDo
				+ " times.");
	}

	private static void mainLoop() throws InterruptedException {
		int requestsDone = 0;
		AtomicLong done = new AtomicLong();
		while (requestsDone++ < totalRequestsToDo) {
			final int id = requestsDone;
			exec.execute(() -> {
				ClientRequestProcessor crp = new ClientRequestProcessor();
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
					crp.connect();
					done.incrementAndGet();
					crp.run();
				} catch (IOException e) {
					// e.printStackTrace();
				} finally {
					crp.close();
				}
			});
		}
		exec.awaitTermination(1, TimeUnit.DAYS);
	}

	private static void statsThread() {
		statsThread = new Thread(new Runnable() {

			@Override
			public void run() {
				long startCounter = finishedCalls.get();
				long startFailed = totalFailedRequests.get();
				while (finishedCalls.get() < totalRequestsToDo) {
					try {
						Thread.sleep(5000);
						System.out.println("Did " + (finishedCalls.get() - startCounter) + " with "
								+ (totalFailedRequests.get() - startFailed) + " failing.");
						startCounter = finishedCalls.get();
						startFailed = totalFailedRequests.get();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				System.out.println("Did " + (finishedCalls.get() - startCounter) + " with "
						+ (totalFailedRequests.get() - startFailed) + " failing.");
			}
		});
		statsThread.start();
	}

}
