package de.oglimmer.web.asynchronous;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;

public enum DataQueue {
	INSTANCE;

	private Set<RequestInformation> requestQueue = new HashSet<>();

	private Executor exec = Executors.newSingleThreadExecutor();

	private long totalContextsAdded;

	private DataQueue() {
		exec.execute(new Loader());
	}

	public void addContext(AsyncContext ac) {
		totalContextsAdded++;
		synchronized (requestQueue) {
			requestQueue.add(new RequestInformation(ac));
		}
	}

	class Loader implements Runnable {

		public void run() {
			// bad never-fail-endless-loop
			while (true) {
				try {
					sendDataToAllRequests();
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private void sendDataToAllRequests() {
			Set<RequestInformation> copy;
			Set<RequestInformation> done = new HashSet<>();

			synchronized (requestQueue) {
				copy = new HashSet<>(requestQueue);
			}

			copy.forEach(e -> e.processRequestChunk(done));

			synchronized (requestQueue) {
				requestQueue.removeAll(done);
			}

		}

	}

	class RequestInformation {

		long id;
		long lastUpdate;
		int chunksSent;
		AsyncContext ac;

		RequestInformation(AsyncContext ac) {
			this.ac = ac;
			this.id = totalContextsAdded;
			this.lastUpdate = System.currentTimeMillis();
		}

		void processRequestChunk(Set<RequestInformation> done) {
			if (id % 5 == 0 && System.currentTimeMillis() - lastUpdate < 1000) {
				return;
			}
			sendData();
			chunksSent++;
			handleCompletion(done);
		}

		void handleCompletion(Set<RequestInformation> done) {
			if (chunksSent >= 5) {
				done.add(this);
				ac.complete();
			}
		}

		void sendData() {
			try {
				ac.getResponse().getWriter().print(".");
				lastUpdate = System.currentTimeMillis();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
