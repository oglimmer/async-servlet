package de.oglimmer.client.post;

public class Config {

    public static Config buildConfig(String[] args) {
        if (args.length < 1) {
            System.out.println(
                    "usage: URL [#-normal-connections #-bad-connections [delay for bad connections [number of calls]]]");
            System.exit(1);
        }

        Config config = new Config();

        if (args[0].startsWith("http")) {
            args[0] = args[0].substring(7);// cut http://
            config.setHost(args[0].substring(0, args[0].indexOf(":")));
            config.setPort(args[0].substring(args[0].indexOf(":") + 1, args[0].indexOf("/")));
            config.setUri(args[0].substring(args[0].indexOf("/")));
        } else {
            config.setUri("/" + args[0]);
        }

        if (args.length > 1) {
            config.setNumberNormalConnections(Integer.parseInt(args[1]));
        }
        if (args.length > 2) {
            config.setNumberBadConnections(Integer.parseInt(args[2]));
        }
        if (args.length > 3) {
            config.setDelay(Integer.parseInt(args[3]));
        }
        if (args.length > 4) {
            config.setTotalNumberCalls(Integer.parseInt(args[4]));
        }
        System.out.println(config);
        return config;
    }

    private int delay = 150;
    private int numberNormalConnections = 5;
    private int numberBadConnections = 5;
    private int totalNumberCalls = Integer.MAX_VALUE;
    private String host = "localhost";
    private String port = "8080";
    private String uri;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getNumberNormalConnections() {
        return numberNormalConnections;
    }

    public void setNumberNormalConnections(int numberNormalConnections) {
        this.numberNormalConnections = numberNormalConnections;
    }

    public int getNumberBadConnections() {
        return numberBadConnections;
    }

    public void setNumberBadConnections(int numberBadConnections) {
        this.numberBadConnections = numberBadConnections;
    }

    public int getTotalNumberCalls() {
        return totalNumberCalls;
    }

    public void setTotalNumberCalls(int totalNumberCalls) {
        this.totalNumberCalls = totalNumberCalls;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String toString() {
        return "Using http://" + host + ":" + port + uri + " with normal " + numberNormalConnections
                + " thread and " + numberBadConnections + " bad thread with delay of " + delay + ".";

    }
}
