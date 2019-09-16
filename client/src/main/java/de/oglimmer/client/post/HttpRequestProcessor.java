package de.oglimmer.client.post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestProcessor {

	private static int idCounter;

	private int delay;
	private int id;

	private long start;
	private boolean failed = false;

	private long lastChunkWrittenAt;
	private int bytesWritten;

	private HttpURLConnection con;
	private OutputStream os;

	private Config config;
	private Content content;

	public HttpRequestProcessor(Config config, Content content, int delay) {
		this.config = config;
		this.content = content;
		this.delay = delay;
		this.id = idCounter++;
	}

	private void log(String s) {
		if (this.id == 0) {
			System.out.println(s);
		}		
	}
	
	public void process() {
		try {
			if (os == null) {
				init();
			}
			sentData();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void close() {

		try {
			if (os != null) {
				os.close();
			}
		} catch (IOException e) {
			if (!"insufficient data written".equals(e.getMessage())) {
				e.printStackTrace();
			}
		}

		if (con != null) {
			con.disconnect();
		}
	}

	private void init() {
		start = System.currentTimeMillis();
		try {
			URL url = new URL("http", config.getHost(), Integer.parseInt(config.getPort()), config.getUri(), null);
			con = (HttpURLConnection) url.openConnection();

			content.generate();

			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setConnectTimeout(100);
			con.setReadTimeout(6000000);
			con.setDefaultUseCaches(false);
			con.setInstanceFollowRedirects(false);
			con.setUseCaches(false);
			log("length=" + content.getPayload().length);
			con.setFixedLengthStreamingMode(content.getPayload().length);
			con.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + content.getBoundry());

			os = con.getOutputStream();
			lastChunkWrittenAt = System.currentTimeMillis();
		} catch (IOException e) {
			failed = true;
			e.printStackTrace();
		}
	}

	private void sentData() {

		if (delay > 0 && System.currentTimeMillis() - lastChunkWrittenAt < delay) {
			return;
		}

		try {
			if (bytesWritten < content.getPayload().length) {
				os.write(content.getPayload()[bytesWritten]);
				bytesWritten++;
				log("process...." + this.id + " / bytesWritten=" + bytesWritten);
				lastChunkWrittenAt = System.currentTimeMillis();
			} else {
				allDataSent();
			}
		} catch (IOException e) {
			failed = true;
			e.printStackTrace();
		}

	}

	private void allDataSent() {
		try {
			try (InputStream is = con.getInputStream()) {
				log("reading response.....");
				String response = readResponse(is);
				if (!response.contains("<title>File Upload Result</title>")) {
					failed = true;
				}
			}
		} catch (IOException e) {
			failed = true;
			e.printStackTrace();
		} finally {
			close();
		}

		Statistics.INSTANCE.totalNumberSinceLastUpdate++;
		Statistics.INSTANCE.timeSpendSinceLastUpdate += (System.currentTimeMillis() - start);
		if (failed) {
			Statistics.INSTANCE.totalFailedSinceLastUpdate++;
		}
		
		Statistics.INSTANCE.totalCount++;

		reset();
	}

	private void reset() {
		start = System.currentTimeMillis();
		failed = false;
		con = null;
		os = null;
		bytesWritten = 0;
		content.reset();
	}

	private String readResponse(InputStream is) throws IOException {
		StringBuffer response = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		return response.toString();
	}

}