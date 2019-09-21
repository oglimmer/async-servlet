package de.oglimmer.client.post;

abstract public class Phase {

    protected RequestProcessor requestProcessor;

    public Phase(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public Phase runPhase() {
        requestProcessor.getExecutionContainer().getRuntimeData().printStats();
        return run();
    }

    abstract public Phase run();
}
