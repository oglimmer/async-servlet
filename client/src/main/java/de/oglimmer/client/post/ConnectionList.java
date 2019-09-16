package de.oglimmer.client.post;

import java.util.ArrayList;
import java.util.List;

public class ConnectionList {

	private List<HttpRequestProcessor> hrp = new ArrayList<>();

	public void addConnection(int delay) {
		hrp.add(new HttpRequestProcessor(delay));
	}

	public List<HttpRequestProcessor> getConnections() {
		return hrp;
	}

}
