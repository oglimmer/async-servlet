package de.oglimmer.client.post;

public class RequestProcessor {

    private ExecutionContainer executionContainer;
    private Phase nextPhase;
    private long delay;
    private int totalExecutions;
    private volatile boolean halt;

    public RequestProcessor(ExecutionContainer executionContainer, int delay) {
        this.executionContainer = executionContainer;
        this.nextPhase = new ConnectPhase(this);
        this.delay = delay;
    }

    public void run() {
        nextPhase = nextPhase.runPhase();
    }

    public long getDelay() {
        return delay;
    }

    public ExecutionContainer getExecutionContainer() {
        return executionContainer;
    }

    public boolean complete() {
        totalExecutions++;
        if (halt || totalExecutions >= executionContainer.getConfiguration().getTotalNumberCalls()) {
            executionContainer.removeRequestProcessor(this);
            return true;
        }
        return false;
    }

    public boolean isHalt() {
        return halt;
    }

    public void requestHalt() {
        halt = true;
    }
}
