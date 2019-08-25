package de.oglimmer.web.synchronous;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/sync")
public class SyncResponse extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private HttpClient client = HttpClient.newHttpClient();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String data = getDataFromBackend();
		forwardData(resp, data);
	}

	private void forwardData(HttpServletResponse resp, String data) {
		try {
			resp.getWriter().print(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getDataFromBackend() {
		try {
			HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:9090/queryResource")).GET().build();
			HttpResponse<String> response = client.send(req, BodyHandlers.ofString());
			return response.body();
		} catch (IOException | URISyntaxException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
