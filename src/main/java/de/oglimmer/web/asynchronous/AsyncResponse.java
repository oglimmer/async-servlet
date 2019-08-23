package de.oglimmer.web.asynchronous;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(asyncSupported = true, urlPatterns = "/async")
public class AsyncResponse extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!req.isAsyncSupported()) {
			System.out.println("no async support!");
		}
		AsyncContext asyncContext = req.startAsync();
		DataQueue.INSTANCE.addContext(asyncContext);
	}

}

enum TimeStats {
	INSTANCE;

	private static final int TIME_CALC_NUMBERS = 500;

	private static AtomicLong counter = new AtomicLong();
	private static AtomicLong totalTime = new AtomicLong();

	public void onComplete(long timeToAdd) {
		counter.incrementAndGet();
		totalTime.addAndGet(timeToAdd);
		if (counter.get() >= TIME_CALC_NUMBERS) {
			System.out.println(TIME_CALC_NUMBERS + " took " + (totalTime.get() / (double) counter.get())
					+ ", current number of threads:" + Thread.activeCount());
			counter.set(0);
			totalTime.set(0);
		}
	}

}