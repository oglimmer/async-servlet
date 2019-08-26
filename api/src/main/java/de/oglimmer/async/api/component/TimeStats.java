package de.oglimmer.async.api.component;

public class TimeStats {

	private static final int TIME_CALC_NUMBERS = 200;

	private long counter = 0;
	private long totalTime = 0;
	private String name;

	public TimeStats(String name) {
		this.name = name;
	}

	public synchronized void onComplete(long timeToAdd) {
		counter++;
		totalTime += timeToAdd;
		if (counter >= TIME_CALC_NUMBERS) {
			System.out.println(name + ":" + counter + " calls took " + (totalTime / (double) counter)
					+ " millies, current number of threads:" + Thread.activeCount());
			counter = 0;
			totalTime = 0;
		}
	}

}