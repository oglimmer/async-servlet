package de.oglimmer.client.post;

import static de.oglimmer.client.post.RandomNameGenerator.randomAlphaNumeric;

public class FileContent extends Content {

    private static final String FILE_CONTENT = "123";

    public void generate() {
        boundry = "--------------" + randomAlphaNumeric(20);
        String tmpbuff1 = "--" + boundry + "\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"file.txt\"\n"
                + "Content-Type: text/plain\n" + "\n" + FILE_CONTENT + ".\n" + "--" + boundry + "--";
        this.payload = tmpbuff1.getBytes();
    }

}
