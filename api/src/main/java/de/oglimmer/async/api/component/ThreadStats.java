package de.oglimmer.async.api.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class ThreadStats {

	private volatile AtomicInteger idGenerator = new AtomicInteger();

	private volatile Map<Integer, Long> activeCounter = Collections.synchronizedMap(new HashMap<>());
	private volatile long totalDoneCounter;
	private volatile long deltaDoneCounter;
	private volatile long timeSpent;

	public int incActive() {
		int id = idGenerator.incrementAndGet();
		if (activeCounter.containsKey(id)) {
			System.err.println("[WARNING] ID " + id + " already active!");
		}
		activeCounter.put(id, System.currentTimeMillis());
		return id;
	}

	public void decActive(int id, boolean count) {
		Long startTime = activeCounter.remove(id);
		if (startTime == null) {
			System.err.println("[WARNING] ID " + id + " was not active!");
		} else {
			timeSpent += System.currentTimeMillis() - startTime;
		}
		if (count) {
			totalDoneCounter++;
			deltaDoneCounter++;
		}
		if (totalDoneCounter % 500 == 0) {
			System.out.println("done 500 ...");
		}
	}

	@PostConstruct
	public void init() {
		Executor exec = Executors.newSingleThreadExecutor();
		exec.execute(new Runnable() {

			@Override
			public void run() {
				while (true) {

					long avgTimeSpent = totalDoneCounter != 0 ? timeSpent / totalDoneCounter : -1;

					System.out.println("Δ-calls: " + deltaDoneCounter + ", active-threads: " + activeCounter.size()
							+ ", total-threads: " + Thread.activeCount() + ", total-calls: " + totalDoneCounter
							+ " (⌀ time spent: " + avgTimeSpent + " millis)");
					deltaDoneCounter = 0;
					try {
						TimeUnit.MILLISECONDS.sleep(10_000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}

		});
	}

}
