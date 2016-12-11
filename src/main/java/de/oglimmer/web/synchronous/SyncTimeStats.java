package de.oglimmer.web.synchronous;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/dataSync")
public class SyncTimeStats implements Filter {

	private static final int TIME_CALC_NUMBERS = 500;

	private static long counter;
	private static long start = -1;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (start == -1) {
			start = System.currentTimeMillis();
		}

		chain.doFilter(request, response);

		counter++;
		if (counter >= TIME_CALC_NUMBERS) {
			counter = 0;
			System.out.println(TIME_CALC_NUMBERS + " took " + (System.currentTimeMillis() - start)
					+ ", current number of threads:" + Thread.activeCount());
			start = System.currentTimeMillis();
		}
	}

	@Override
	public void destroy() {

	}

}
