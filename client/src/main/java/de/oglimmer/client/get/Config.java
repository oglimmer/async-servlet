package de.oglimmer.client.get;

public class Config {

	private int maxNumberThreads = 500;
	private long totalRequestsToDo = 500;
	private String url;

	public int getMaxNumberThreads() {
		return maxNumberThreads;
	}

	public void setMaxNumberThreads(int maxNumberThreads) {
		this.maxNumberThreads = maxNumberThreads;
	}

	public long getTotalRequestsToDo() {
		return totalRequestsToDo;
	}

	public void setTotalRequestsToDo(long totalRequestsToDo) {
		this.totalRequestsToDo = totalRequestsToDo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString() {
		return "Using " + getUrl() + " with " + getMaxNumberThreads() + " thread, calling it " + getTotalRequestsToDo()
				+ " times.";
	}

}
