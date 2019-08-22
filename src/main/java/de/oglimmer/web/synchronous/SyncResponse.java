package de.oglimmer.web.synchronous;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(asyncSupported = true, urlPatterns = "/dataSync")
public class SyncResponse extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static long counter;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		send5Dots(resp, counter);
		counter++;
	}

	private void send5Dots(HttpServletResponse resp, long id) {
		for (int i = 0; i < 5; i++) {
			sendDot(resp);
			simulateFrquentlySlowBackend(id);
		}
	}

	private void simulateFrquentlySlowBackend(long id) {
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	private void sendDot(HttpServletResponse resp) {
		try {
			resp.getWriter().print(".");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
