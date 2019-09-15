package de.oglimmer.async.api.component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class ThreadStats {

	private volatile AtomicInteger idGenerator = new AtomicInteger();

	private volatile Set<Integer> activeCounter = Collections.synchronizedSet(new HashSet<>());
	private volatile long totalDoneCounter;
	private volatile long deltaDoneCounter;
	private volatile long byteCounter;
	private volatile long timeSpent;

	public void incTime(long time) {
		timeSpent += time;
	}

	public int incActive() {
		int id = idGenerator.incrementAndGet();
		if (activeCounter.contains(id)) {
			System.out.println("[WARNING] ID " + id + " already active!");
		}
		activeCounter.add(id);
		return id;
	}

	public void decActive(int id) {
		if (!activeCounter.remove(id)) {
			System.out.println("[WARNING] ID " + id + " was not active!");
		}
	}

	public void incAll(long time, long bytes) {
		totalDoneCounter++;
		deltaDoneCounter++;
		byteCounter += bytes;
		timeSpent += time;
	}

	@PostConstruct
	public void init() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					long avgTimeSpent = totalDoneCounter != 0 ? timeSpent / totalDoneCounter : -1;

					System.out.println("Since last row: updates=" + deltaDoneCounter + ", bytes=" + byteCounter
							+ ", currently active: uploads=" + activeCounter.size() + ", threads="
							+ Thread.activeCount() + ", total thread time/count: " + timeSpent + "milli / "
							+ totalDoneCounter + " (" + avgTimeSpent + "milli)");
					deltaDoneCounter = 0;
					byteCounter = 0;
					try {
						TimeUnit.MILLISECONDS.sleep(10_000);
					} catch (InterruptedException e) {
					}
				}
			}

		}).start();
	}

}
