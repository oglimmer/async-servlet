package de.oglimmer.client.post;

import static de.oglimmer.client.post.RandomNameGenerator.randomAlphaNumeric;

public class FieldContent extends Content {

    private final static String VAR_VALUE = "bar";

    public void generate() {
        boundry = "--------------" + randomAlphaNumeric(20);
        String tmpbuff1 = "--" + boundry + "\n"
                + "Content-Disposition: form-data; name=\"foo\" \n"
                + "\n" + VAR_VALUE + ".\n" + "--" + boundry + "--";
        this.payload = tmpbuff1.getBytes();
    }

}
