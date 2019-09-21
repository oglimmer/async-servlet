package de.oglimmer.client.post;

import java.io.IOException;

public class WriteDataPhase extends Phase {

    private Connection connection;
    private int bytesWritten;
    private long lastByteWrittenAt;

    public WriteDataPhase(RequestProcessor requestProcessor, Connection connection) {
        super(requestProcessor);
        this.connection = connection;
    }

    @Override
    public Phase run() {
        long delay = requestProcessor.getDelay();
        if (!requestProcessor.isHalt() && delay > 0 && System.currentTimeMillis() - lastByteWrittenAt < delay) {
            return this;
        }
        lastByteWrittenAt = System.currentTimeMillis();

        try {
            byte[] payload = requestProcessor.getExecutionContainer().getConfiguration().getContent().getPayload();
            if (bytesWritten < payload.length) {
                connection.getOutputStream().write(payload[bytesWritten]);
                bytesWritten++;
            } else {
                return new ReadDataPhase(requestProcessor, connection);
            }
        } catch (IOException e) {
            return new FailedPhase(e, connection, requestProcessor);
        }
        return this;
    }
}
