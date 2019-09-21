package de.oglimmer.async.api.service;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import de.oglimmer.async.api.component.ThreadStats;
import reactor.core.publisher.Mono;

@WebServlet(urlPatterns = {"/asyncServlet"}, asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private WebClient client = WebClient.create("http://localhost:9090/queryResource");

    @Autowired
    private ThreadStats threadStats;

    /**
     * Access via client/get.sh http://localhost:8080/asyncServlet
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = threadStats.incActive();
        AsyncContext context = request.startAsync();
        Mono<String> mono = client.get().retrieve().bodyToMono(String.class);
        mono.subscribe(success -> {
            threadStats.decActive(id, true);
            try {
                context.getResponse().getWriter().println(success);
            } catch (IOException e) {
                e.printStackTrace();
            }
            context.complete();
        });
    }

    /**
     * Access via client/post.sh http://localhost:8080/asyncServlet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = threadStats.incActive();
        AsyncContext context = request.startAsync();
        ServletInputStream inputStream = request.getInputStream();
        try {
            inputStream.setReadListener(new BufferedReadListenerImpl(inputStream, context, params -> {
                String param = params.get("foo");
                boolean success = false;
                if (param != null && param.length() == 512) {
                    success = true;
                }
                try {
                    context.getResponse().getWriter().println(success ? "done" : "error");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                threadStats.decActive(id, true);
            }, VOID -> {
                threadStats.decActive(id, true);
            }));
        } catch (IllegalStateException e) {
            threadStats.decActive(id, true);
            throw e;
        }
    }
}

class BufferedReadListenerImpl implements ReadListener {

    private ServletInputStream inputStream;
    private AsyncContext asyncCtxt;

    private Consumer<Map<String, String>> success;
    private Consumer<Void> failed;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public BufferedReadListenerImpl(ServletInputStream inputStream, AsyncContext asyncCtxt,
                                    Consumer<Map<String, String>> success, Consumer<Void> failed) {
        this.inputStream = inputStream;
        this.asyncCtxt = asyncCtxt;
        this.success = success;
        this.failed = failed;
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
