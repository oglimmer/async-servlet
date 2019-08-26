package de.oglimmer.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientRequestProcessor {

	private static final int READ_TIMEOUT = 15000;
	private static final int CONNECTION_TIMEOUT = 2000;

	private long start;
	private HttpURLConnection con;
	private Client client;

	public ClientRequestProcessor(Client client) {
		this.client = client;
	}

	public void connect(String url) throws IOException {
		start = System.currentTimeMillis();
		try {
			URL obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();
			con.setConnectTimeout(CONNECTION_TIMEOUT);
			con.setReadTimeout(READ_TIMEOUT);
		} catch (IOException e) {
			client.getStatistics().getTotalFailedRequests().incrementAndGet();
			throw new IOException(e);
		}
	}

	public void run() throws IOException {
		try {
			String response = readResponse(con);
			if (!"<backend-data>".equals(response)) {
				client.getStatistics().getTotalFailedRequests().incrementAndGet();
			}
		} catch (IOException e) {
			client.getStatistics().getTotalFailedRequests().incrementAndGet();
			throw new IOException(e);
		}
	}

	public void close() {
		con.disconnect();
		long totalTime = System.currentTimeMillis() - start;
		Statistics statistics = client.getStatistics();
		statistics.getTotalTimeSpent().addAndGet(totalTime);
		if (statistics.getMaxTimeSpent().get() < totalTime) {
			statistics.getMaxTimeSpent().set(totalTime);
		}
		statistics.getFinishedCalls().incrementAndGet();
		if (statistics.getFinishedCalls().get() == client.getTotalRequestsToDo()) {
			client.getExecutorService().shutdown();
			statistics.interrupt();
		}
	}

	private String readResponse(HttpURLConnection con) throws IOException {
		StringBuffer response = new StringBuffer();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		return response.toString();
	}

}
