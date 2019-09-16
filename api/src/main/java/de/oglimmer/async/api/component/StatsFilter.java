package de.oglimmer.async.api.component;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatsFilter implements Filter {

	@Autowired
	private ThreadStats threadStats;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		int id = threadStats.incActive();
		chain.doFilter(request, response);
		boolean count = false;
		if (request instanceof HttpServletRequest) {
			count = !((HttpServletRequest) request).getRequestURI().contains("async");
		}
		threadStats.decActive(id, count);
	}

}
