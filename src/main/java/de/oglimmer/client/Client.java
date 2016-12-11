package de.oglimmer.client;

public class Client {

	private static final int TOTAL_REQUESTS = 50000000;
	private static final int MAX_NUMBER_THREADS = 500;

	static String url;

	volatile static long runningThreadCounter;
	volatile static long totalTimeSpent;
	volatile static long totalFailedRequests;

	public static void main(String[] args) {
		buildUrl(args);
		mainLoop();
	}

	private static void buildUrl(String[] args) {
		if (args.length < 1 || !("dataSync".equals(args[0]) || "dataAsync".equals(args[0]))) {
			System.out.println("Start with parameter dataSync or dataAsync");
			System.exit(1);
		}
		url = "http://localhost:8080/" + args[0];
		System.out.println("Using " + url + " with " + MAX_NUMBER_THREADS + " thread");
	}

	private static void mainLoop() {
		int totalThreadsCreatedCounter = 0;
		int threadsCreatedSinceLastTimeReset = 0;
		while (totalThreadsCreatedCounter < TOTAL_REQUESTS) {
			totalThreadsCreatedCounter++;
			threadsCreatedSinceLastTimeReset++;
			createThread();
			waitIfTooManyThreadRunning();
			threadsCreatedSinceLastTimeReset = timeStats(totalThreadsCreatedCounter, threadsCreatedSinceLastTimeReset);
		}
	}

	private static int timeStats(int totalThreadsCreatedCounter, int threadsCreatedSinceLastTimeReset) {
		if (totalThreadsCreatedCounter % 500 == 0) {
			System.out.println("Avg time: " + (totalTimeSpent / threadsCreatedSinceLastTimeReset)
					+ " with Failed requests " + totalFailedRequests);
			totalTimeSpent = 0;
			threadsCreatedSinceLastTimeReset = 0;
			totalFailedRequests = 0;
		}
		return threadsCreatedSinceLastTimeReset;
	}

	private static void waitIfTooManyThreadRunning() {
		if (runningThreadCounter > MAX_NUMBER_THREADS) {
			synchronized (ClientRequestProcessor.class) {
				try {
					ClientRequestProcessor.class.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static void createThread() {
		Thread thread = new Thread(new ClientRequestProcessor());
		thread.start();
		runningThreadCounter++;
	}

	private Client() {
	}
}
