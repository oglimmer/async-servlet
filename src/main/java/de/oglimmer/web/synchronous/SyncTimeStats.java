package de.oglimmer.web.synchronous;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/sync")
public class SyncTimeStats implements Filter {

	private static final int TIME_CALC_NUMBERS = 500;

	private static AtomicLong counter = new AtomicLong();
	private static AtomicLong totalTime = new AtomicLong();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		long start = System.currentTimeMillis();
		chain.doFilter(request, response);
		counter.incrementAndGet();
		totalTime.addAndGet(System.currentTimeMillis() - start);
		if (counter.get() >= TIME_CALC_NUMBERS) {
			System.out.println(TIME_CALC_NUMBERS + " took " + (totalTime.get() / (double) counter.get())
					+ ", current number of threads:" + Thread.activeCount());
			counter.set(0);
			totalTime.set(0);
		}
	}

	@Override
	public void destroy() {

	}

}
