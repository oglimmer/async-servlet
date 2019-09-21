package de.oglimmer.client.post;

public class RuntimeData {

    private static final int INTERVAL = 5000;

    private ExecutionContainer executionContainer;

    private long lastFlush = System.currentTimeMillis();

    private long totalFailedSinceLastFlush;
    private long totalNumberSinceLastFlush;
    private long timeSpendSinceLastFlush;

    private long timeSpendTotal;
    private long countTotal;
    private long countFailedTotal;

    public RuntimeData(ExecutionContainer executionContainer) {
        this.executionContainer = executionContainer;
    }

    public void success(long time) {
        timeSpendSinceLastFlush += time;
        timeSpendTotal += time;
        countTotal++;
        totalNumberSinceLastFlush++;
    }

    public void failed() {
        totalFailedSinceLastFlush++;
        countFailedTotal++;
    }

    public void printStats() {
        if (lastFlush > System.currentTimeMillis() - INTERVAL) {
            return;
        }
        long timeSinceLastFlush = System.currentTimeMillis() - lastFlush;
        lastFlush = System.currentTimeMillis();
        long avgNormal = calcAvgProcessingTime();
        System.out.println("In " + timeSinceLastFlush + " millies, in total "
                + totalNumberSinceLastFlush + " done, " + totalFailedSinceLastFlush
                + " failed and average processing time was " + avgNormal + "millies. Running ExecutionContainers " + executionContainer.getRequestProcessorsSize());
        resetStats();
    }

    public void printEndStats() {
        System.out.println("***********END***********");
        System.out.println("Total calls done: " + countTotal + " with " + countFailedTotal + " failed and avg time of " + (timeSpendTotal / countTotal) + " millies.");
    }

    private void resetStats() {
        timeSpendSinceLastFlush = 0;
        totalNumberSinceLastFlush = 0;
        totalFailedSinceLastFlush = 0;
    }

    private long calcAvgProcessingTime() {
        long avgNormal = -1;
        if (totalNumberSinceLastFlush > 0) {
            avgNormal = timeSpendSinceLastFlush / totalNumberSinceLastFlush;
        }
        return avgNormal;
    }

}
