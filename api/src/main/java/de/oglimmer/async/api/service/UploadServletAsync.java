package de.oglimmer.async.api.service;

import de.oglimmer.async.api.component.ThreadStats;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.EOFException;
import java.io.IOException;

/**
 * Access via client/upload.sh http://localhost:8080/uploadServletAsync
 */
@WebServlet(urlPatterns = {"/uploadServletAsync"}, asyncSupported = true)
public class UploadServletAsync extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    private ThreadStats threadStats;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = threadStats.incActive();
        AsyncContext context = request.startAsync();
        ServletInputStream inputStream = request.getInputStream();
        inputStream.setReadListener(new ReadListenerImpl(threadStats, inputStream, response, context, id));
    }
}

class ReadListenerImpl implements ReadListener {

    private ThreadStats threadStats;
    private ServletInputStream inputStream = null;
    private HttpServletResponse response = null;
    private AsyncContext asyncCtxt = null;
    private int id;

    ReadListenerImpl(ThreadStats threadStats, ServletInputStream inputStream, HttpServletResponse response, AsyncContext asyncCtxt, int id) {
        this.threadStats = threadStats;
        this.inputStream = inputStream;
        this.response = response;
        this.asyncCtxt = asyncCtxt;
        this.id = id;
    }

    @Override
    public void onDataAvailable() throws IOException {
        long totalBytes = 0;
        try {
            int len;
            byte byteBuff[] = new byte[1024];
            while (inputStream.isReady() && (len = inputStream.read(byteBuff)) != -1) {
                // throw it away
                totalBytes += len;
            }
        } catch (EOFException e) {
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void onAllDataRead() throws IOException {
        response.getWriter().println("<title>File Upload Result</title>");
        asyncCtxt.complete();
        threadStats.decActive(id, true);
    }

    @Override
    public void onError(final Throwable t) {
        asyncCtxt.complete();
        t.printStackTrace();
        threadStats.decActive(id, true);
    }

}