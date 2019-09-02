package de.oglimmer.async.api.component;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Component;

@Component
public class FakeBackendUri {

    public URI get() {
        try {
            return new URI("http://localhost:9090/queryResource");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}


