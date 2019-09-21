package de.oglimmer.client.post;

public class FailedPhase extends Phase {

    private Throwable e;
    private Connection connection;

    public FailedPhase(Throwable e, Connection connection, RequestProcessor requestProcessor) {
        super(requestProcessor);
        this.e = e;
        this.connection = connection;
    }

    @Override
    public Phase run() {
        requestProcessor.getExecutionContainer().getRuntimeData().failed();
        // e.printStackTrace();
        return new CompletionPhase(requestProcessor, connection);
    }
}
