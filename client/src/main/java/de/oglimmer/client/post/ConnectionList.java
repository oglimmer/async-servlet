package de.oglimmer.client.post;

import java.util.ArrayList;
import java.util.List;

public class ConnectionList {

	private List<HttpRequestProcessor> hrp = new ArrayList<>();

	public void addConnection(Config config, Content content, int delay) {
		hrp.add(new HttpRequestProcessor(config, content, delay));
	}

	public List<HttpRequestProcessor> getConnections() {
		return hrp;
	}

}
