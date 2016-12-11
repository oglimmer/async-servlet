package de.oglimmer.web.asynchronous;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(asyncSupported = true, urlPatterns = "/dataAsync")
public class AsyncResponse extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final DebugAsyncListener dal = new DebugAsyncListener();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (!req.isAsyncSupported()) {
			System.out.println("no async support!");
		}

		AsyncContext asyncContext = req.startAsync();
		DataQueue.INSTANCE.addContext(asyncContext);
		asyncContext.addListener(dal);
	}

	static class DebugAsyncListener implements AsyncListener {

		@Override
		public void onTimeout(AsyncEvent event) throws IOException {
			System.out.println("onTimeout");
		}

		@Override
		public void onStartAsync(AsyncEvent event) throws IOException {
			System.out.println("onStartAsync");
		}

		@Override
		public void onError(AsyncEvent event) throws IOException {
			System.out.println("onError");
		}

		@Override
		public void onComplete(AsyncEvent event) throws IOException {
			TimeStats.INSTANCE.onComplete();
		}
	}

}

enum TimeStats {
	INSTANCE;

	private static final int TIME_CALC_NUMBERS = 500;

	private static long counter;
	private static long start = System.currentTimeMillis();

	public void onComplete() {
		counter++;
		if (counter >= TIME_CALC_NUMBERS) {
			counter = 0;
			System.out.println(TIME_CALC_NUMBERS + " took " + (System.currentTimeMillis() - start)
					+ ", current number of threads:" + Thread.activeCount());
			start = System.currentTimeMillis();
		}
	}

}