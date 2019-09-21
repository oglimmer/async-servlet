package de.oglimmer.client.post;

public class CompletionPhase extends Phase {

    private Connection connection;

    public CompletionPhase(RequestProcessor requestProcessor, Connection connection) {
        super(requestProcessor);
        this.connection = connection;
    }

    @Override
    public Phase run() {
        connection.close();
        requestProcessor.getExecutionContainer().getRuntimeData().success(connection.getTotalTime());
        if (requestProcessor.complete()) {
            return null;
        }
        return new ConnectPhase(requestProcessor);
    }
}
