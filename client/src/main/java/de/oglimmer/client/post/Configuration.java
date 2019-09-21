package de.oglimmer.client.post;

public class Configuration {

    public static Configuration buildConfig(String[] args) {
        if (args.length < 2) {
            System.out.println(
                    "usage: URL [#-normal-connections #-bad-connections [delay for bad connections [number of calls]]]");
            System.exit(1);
        }

        Configuration config = new Configuration();

        String formType = args[0];
        config.contentProvider = ContentProvider.getInstance(formType);

        String argUrl = args[1];
        if (argUrl.startsWith("http")) {
            argUrl = argUrl.substring(7);// cut http://
            config.setHost(argUrl.substring(0, argUrl.indexOf(":")));
            config.setPort(Integer.parseInt(argUrl.substring(argUrl.indexOf(":") + 1, argUrl.indexOf("/"))));
            config.setUri(argUrl.substring(argUrl.indexOf("/")));
        } else {
            throw new RuntimeException("url must start with http");
        }

        if (args.length > 2) {
            config.numberNormalConnections = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            config.numberBadConnections = Integer.parseInt(args[3]);
        }
        if (args.length > 4) {
            config.delay = Integer.parseInt(args[4]);
        }
        if (args.length > 5) {
            config.totalNumberCalls = Integer.parseInt(args[5]);
        }
        System.out.println(config);
        return config;
    }

    private ContentProvider contentProvider;

    private String host;
    private int port;
    private String uri;

    private int numberNormalConnections = 1;
    private int numberBadConnections = 0;
    private int totalNumberCalls = Integer.MAX_VALUE;

    private int delay = 100;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public ContentProvider getContent() {
        return contentProvider;
    }

    public int getDelay() {
        return delay;
    }

    public int getNumberNormalConnections() {
        return numberNormalConnections;
    }

    public int getNumberBadConnections() {
        return numberBadConnections;
    }

    public int getTotalNumberCalls() {
        return totalNumberCalls;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", uri='" + uri + '\'' +
                ", numberNormalConnections=" + numberNormalConnections +
                ", numberBadConnections=" + numberBadConnections +
                ", totalNumberCalls=" + totalNumberCalls +
                ", delay=" + delay +
                '}';
    }
}
