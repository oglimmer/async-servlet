package de.oglimmer.client.post;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * <ol>
 * <li>Build config from defaults/cli-params</li>
 * <li>Use main thread to execute http post calls with blocking but super mini (1 byte) writes</li>
 * <li>Each connection and reads back the result then terminates</li>
 * </ol>
 */
public class Startup {

	private Thread statsThread;
	private Config config;

	private volatile ConnectionList ce = new ConnectionList();

	public static void main(String[] args) {
		Config config = Config.buildConfig(args);
		new Startup(config);
	}

	public Startup(Config config) {
		this.config = config;
		initConnections();
		createShutdownHook();
		createStatsThread();
		mainLoop();
		shutdown();
	}

	private void shutdown() {
		for (Iterator<HttpRequestProcessor> it = ce.getConnections().iterator(); it.hasNext();) {
			HttpRequestProcessor hrp = it.next();
			hrp.close();
			it.remove();
		}
		statsThread.stop();
	}

	private void mainLoop() {
		try {
			while (Statistics.INSTANCE.totalCount < config.getTotalNumberCalls()) {
				ce.getConnections().forEach(HttpRequestProcessor::process);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void initConnections() {
		for (int i = 0; i < config.getNumberNormalConnections(); i++) {
			ce.addConnection(config, new FileContent(),0);
		}
		for (int i = 0; i < config.getNumberBadConnections(); i++) {
			ce.addConnection(config, new FileContent(), config.getDelay());
		}
	}

	private void createStatsThread() {
		statsThread = new Thread(new StatisticsThread());
		statsThread.start();
	}

	private void createShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownThread(ce)));
	}

}

class ShutdownThread implements Runnable {

	private ConnectionList ce;

	public ShutdownThread(ConnectionList ce ) {
		this.ce = ce;
	}

	public void run() {
		Thread.currentThread().setName("ShutdownHook-Thread");
		System.out.println("shutting down..");

		while (!ce.getConnections().isEmpty()) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("shutdown completed.");
	}
}