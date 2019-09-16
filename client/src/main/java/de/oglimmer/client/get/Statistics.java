package de.oglimmer.client.get;

import java.util.concurrent.atomic.AtomicLong;

public class Statistics implements Runnable {

	private AtomicLong finishedCalls = new AtomicLong();
	private AtomicLong totalTimeSpent = new AtomicLong();
	private AtomicLong maxTimeSpent = new AtomicLong();
	private AtomicLong totalFailedRequests = new AtomicLong();
	private Thread statsThread;
	private Client client;

	public Statistics(Client client) {
		this.client = client;
	}

	public void start() {
		statsThread = new Thread(this);
		statsThread.start();
	}

	@Override
	public void run() {
		long startCounter = finishedCalls.get();
		long startFailed = totalFailedRequests.get();
		while (finishedCalls.get() < client.getConfig().getTotalRequestsToDo()) {
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
				+ (totalFailedRequests.get() - startFailed) + " failing. Max time: " + maxTimeSpent.get());
	}

	public void print() {
		System.out.println("Processing took " + (totalTimeSpent.get() / (double) finishedCalls.get())
				+ " msec in average and it failed for " + totalFailedRequests.get());
	}

	public AtomicLong getFinishedCalls() {
		return finishedCalls;
	}

	public AtomicLong getTotalTimeSpent() {
		return totalTimeSpent;
	}

	public AtomicLong getMaxTimeSpent() {
		return maxTimeSpent;
	}

	public AtomicLong getTotalFailedRequests() {
		return totalFailedRequests;
	}

	public void interrupt() {
		statsThread.interrupt();
	}

}
