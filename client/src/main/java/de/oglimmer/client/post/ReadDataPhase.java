package de.oglimmer.client.post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadDataPhase extends Phase {

    private Connection connection;

    public ReadDataPhase(RequestProcessor requestProcessor, Connection connection) {
        super(requestProcessor);
        this.connection = connection;
    }

    @Override
    public Phase run() {
        try {
            try (InputStream is = connection.getConnection().getInputStream()) {
                String response = readResponse(is);
                if (!requestProcessor.getExecutionContainer().getConfiguration().getContent().isResultOk(response)) {
                    return new FailedPhase(new RuntimeException("Wrong content: " + response), connection, requestProcessor);
                }
            }
        } catch (IOException e) {
            return new FailedPhase(e, connection, requestProcessor);
        }
        return new CompletionPhase(requestProcessor, connection);
    }


    private String readResponse(InputStream is) throws IOException {
        StringBuffer response = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        return response.toString();
    }
}
