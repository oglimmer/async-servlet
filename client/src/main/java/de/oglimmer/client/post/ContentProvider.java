package de.oglimmer.client.post;

import static de.oglimmer.client.post.RandomNameGenerator.randomAlphaNumeric;

abstract public class ContentProvider {

    public static ContentProvider getInstance(String type) {
        switch (type) {
            case "Field":
                return new FieldContentGenerator();
            case "File":
                return new FileContentGenerator();
        }
        return null;
    }

    abstract public byte[] getPayload();

    abstract public String getContentType();

    public abstract boolean isResultOk(String response);
}

class FieldContentGenerator extends ContentProvider {

    private byte[] payload;

    public FieldContentGenerator() {
        payload = ("foo=" + randomAlphaNumeric(512)).getBytes();
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public String getContentType() {
        return "application/x-www-form-urlencoded";
    }

    @Override
    public boolean isResultOk(String response) {
        return "done".equals(response);
    }
}


class FileContentGenerator extends ContentProvider {

    private static final String CRNL = "\r\n";

    private static final String FILE_CONTENT = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Ste";
    private String boundary;
    private byte[] payload;

    public FileContentGenerator() {
        boundary = "--------------" + randomAlphaNumeric(20);
        String tmp = "--" + boundary + CRNL
                + "Content-Disposition: form-data; name=\"file\"; filename=\"file.txt\"" + CRNL
                + "Content-Type: text/plain" + CRNL
                + CRNL
                + FILE_CONTENT + CRNL
                + "--" + boundary + "--" + CRNL;
        this.payload = tmp.getBytes();

    }

    @Override
    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }


    @Override
    public byte[] getPayload() {
        return this.payload;
    }

    @Override
    public boolean isResultOk(String response) {
        return response.contains("<title>File Upload Result</title>");
    }
}
