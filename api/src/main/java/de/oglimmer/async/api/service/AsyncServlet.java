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
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@WebServlet(urlPatterns = { "/asyncPost" }, asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    private ThreadStats threadStats;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = threadStats.incActive();
        AsyncContext context = request.startAsync();
        ServletInputStream inputStream = request.getInputStream();
        try {
            inputStream.setReadListener(new BufferedReadListenerImpl(threadStats, inputStream, context, params -> {
                String param = params.get("foo");
                if (!("oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        + "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        + "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        + "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        + "oooooooooooooooooooooooooooooooooooooooooooooooooooooo").equals(param)) {
                    System.out.println("failed to get param: " + param);
                }
                try {
                    context.getResponse().getWriter().println("done");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                threadStats.decActive(id);
            }, VOID -> {
                threadStats.decActive(id);
            }));
        } catch (IllegalStateException e) {
            threadStats.decActive(id);
            throw e;
        }
    }
}

class BufferedReadListenerImpl implements ReadListener {

    private ServletInputStream inputStream;
    private AsyncContext asyncCtxt;
    private long start;

    private Consumer<Map<String, String>> success;
    private Consumer<Void> failed;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private ThreadStats threadStats;

    public BufferedReadListenerImpl(ThreadStats threadStats, ServletInputStream inputStream, AsyncContext asyncCtxt,
                                    Consumer<Map<String, String>> success, Consumer<Void> failed) {
        this.threadStats = threadStats;
        this.inputStream = inputStream;
        this.asyncCtxt = asyncCtxt;
        this.success = success;
        this.failed = failed;
        this.start = System.currentTimeMillis();
    }

    @Override
    public void onDataAvailable() throws IOException {
        try {
            int len;
            byte byteBuff[] = new byte[1024];
            while (inputStream.isReady() && (len = inputStream.read(byteBuff)) != -1) {
                buffer.write(byteBuff, 0, len);
            }
        } catch (EOFException e) {
            failed.accept(null);
        } catch (IOException e) {
            failed.accept(null);
            throw e;
        }
    }

    @Override
    public void onAllDataRead() throws IOException {
        threadStats.incAll(System.currentTimeMillis() - start, buffer.size());
        success.accept(transform());
        asyncCtxt.complete();
    }

    private Map<String, String> transform() {
        Map<String, String> postParams = new HashMap<>();
        for (String keyValue : buffer.toString().split("\\&")) {
            String[] keyValues = keyValue.split("=");
            if (keyValues.length > 0) {
                String key = keyValues[0];
                String val = keyValues.length > 1 ? keyValues[1] : null;
                postParams.put(key, val);
            }
        }
        return postParams;
    }

    @Override
    public void onError(final Throwable t) {
        t.printStackTrace();
        failed.accept(null);
        asyncCtxt.complete();
    }

}
