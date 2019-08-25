package de.oglimmer.web.asynchronous;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(asyncSupported = true, urlPatterns = "/async")
public class AsyncResponse extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private HttpClient client = HttpClient.newBuilder().executor(Executors.newFixedThreadPool(20)).build();

	private static URI uri;
	static {
		try {
			uri = new URI("http://localhost:9090/queryResource");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!req.isAsyncSupported()) {
			System.out.println("no async support!");
		}
		AsyncContext asyncContext = req.startAsync();
		final long start = System.currentTimeMillis();
		HttpRequest backendReq = HttpRequest.newBuilder(uri).GET().build();
		client.sendAsync(backendReq, BodyHandlers.ofString()).thenAccept(response -> {
			String data = response.body();
			try {
				asyncContext.getResponse().getWriter().write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			asyncContext.complete();
			TimeStats.INSTANCE.onComplete(System.currentTimeMillis() - start);
		});
	}

}

enum TimeStats {
	INSTANCE;

	private static final int TIME_CALC_NUMBERS = 500;

	private static long counter = 0;
	private static long totalTime = 0;

	public synchronized void onComplete(long timeToAdd) {
		counter++;
		totalTime += timeToAdd;
		if (counter >= TIME_CALC_NUMBERS) {
			System.out.println(TIME_CALC_NUMBERS + " took " + (totalTime / (double) counter)
					+ ", current number of threads:" + Thread.activeCount());
			counter = 0;
			totalTime = 0;
		}
	}

}