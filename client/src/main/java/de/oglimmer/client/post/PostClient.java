package de.oglimmer.client.post;

public class PostClient {

    public static final void main(String... args) {
        Configuration configuration = Configuration.buildConfig(args);
        ExecutionContainer er = new ExecutionContainer(configuration);
        er.run();
        er.getRuntimeData().printEndStats();
    }

}
