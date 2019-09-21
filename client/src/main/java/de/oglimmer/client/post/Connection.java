package de.oglimmer.client.post;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class Connection {

    private HttpURLConnection con;
    private OutputStream os;

    private long start;

    public Connection() {
        this.start = System.currentTimeMillis();
    }

    public void setConnection(HttpURLConnection con) {
        this.con = con;
    }

    public HttpURLConnection getConnection() {
        return con;
    }

    public void setOutputStream(OutputStream os) {
        this.os = os;
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public long getTotalTime() {
        return System.currentTimeMillis() - start;
    }

    public void close() {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (con != null) {
            con.disconnect();
        }
    }


}
