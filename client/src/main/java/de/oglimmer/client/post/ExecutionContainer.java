package de.oglimmer.client.post;

import java.util.ArrayList;
import java.util.List;

public class ExecutionContainer {

    private Configuration configuration;
    private RuntimeData runtimeData = new RuntimeData(this);
    private List<RequestProcessor> requestProcessorList = new ArrayList<>();

    public ExecutionContainer(Configuration configuration) {
        this.configuration = configuration;
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownThread()));
    }

    public void run() {
        for (int i = 0; i < configuration.getNumberNormalConnections(); i++) {
            requestProcessorList.add(new RequestProcessor(this, 0));
        }
        for (int i = 0; i < configuration.getNumberBadConnections(); i++) {
            requestProcessorList.add(new RequestProcessor(this, configuration.getDelay()));
        }

        while (!requestProcessorList.isEmpty()) {
            List<RequestProcessor> workingList = new ArrayList<>(requestProcessorList);
            workingList.forEach(RequestProcessor::run);
        }
    }

    public void removeRequestProcessor(RequestProcessor requestProcessor) {
        requestProcessorList.remove(requestProcessor);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public RuntimeData getRuntimeData() {
        return runtimeData;
    }

    public int getRequestProcessorsSize() {
        return requestProcessorList.size();
    }

    class ShutdownThread implements Runnable {
        @Override
        public void run() {
            requestProcessorList.forEach(RequestProcessor::requestHalt);
            while (!requestProcessorList.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
