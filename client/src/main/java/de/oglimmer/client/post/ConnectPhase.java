package de.oglimmer.client.post;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectPhase extends Phase {

    public ConnectPhase(RequestProcessor requestProcessor) {
        super(requestProcessor);
    }

    @Override
    public Phase run() {
        Connection connection = new Connection();
        try {
            Configuration configuration = requestProcessor.getExecutionContainer().getConfiguration();
            URL url = new URL("http", configuration.getHost(), configuration.getPort(), configuration.getUri(), null);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            connection.setConnection(con);

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setConnectTimeout(100);
            con.setReadTimeout(6000000);
            con.setDefaultUseCaches(false);
            con.setInstanceFollowRedirects(false);
            con.setUseCaches(false);

            con.setFixedLengthStreamingMode(configuration.getContent().getPayload().length);
            con.addRequestProperty("Content-Type", configuration.getContent().getContentType());

            OutputStream os = con.getOutputStream();
            connection.setOutputStream(os);

            return new WriteDataPhase(requestProcessor, connection);
        } catch (IOException e) {
            return new FailedPhase(e, connection, requestProcessor);
        }
    }
}
