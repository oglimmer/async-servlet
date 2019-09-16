package de.oglimmer.client.post;

public abstract class Content {

    protected byte[] payload;
    protected String boundry;

    public void reset() {
        boundry = null;
        payload = null;
    }

    public abstract void generate();

    public String getBoundry() {
        return boundry;
    }

    public byte[] getPayload() {
        return payload;
    }
}
