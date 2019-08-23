package de.oglimmer.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientRequestProcessor {

	long start;
	HttpURLConnection con;

	public void connect() throws IOException {
		start = System.currentTimeMillis();
		try {
			URL obj = new URL(Client.url);
			con = (HttpURLConnection) obj.openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(8000);
		} catch (IOException e) {
			Client.totalFailedRequests.incrementAndGet();
			throw new IOException(e);
		}
	}

	public void run() throws IOException {
		try {
			String response = readResponse(con);
			if (!".....".equals(response)) {
				Client.totalFailedRequests.incrementAndGet();
			}
		} catch (IOException e) {
			Client.totalFailedRequests.incrementAndGet();
			throw new IOException(e);
		}
	}

	public void close() {
		con.disconnect();
		Client.totalTimeSpent.addAndGet(System.currentTimeMillis() - start);
		Client.finishedCalls.incrementAndGet();
		if (Client.finishedCalls.get() == Client.totalRequestsToDo) {
			Client.exec.shutdown();
			Client.statsThread.interrupt();
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
