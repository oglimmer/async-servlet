package de.oglimmer.async.api.service;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.EOFException;
import java.io.IOException;

/**
 * Access via client/upload.sh http://localhost:8080/uploadServletSync
 */
@WebServlet(urlPatterns = {"/uploadServletSync"}, asyncSupported = false)
public class UploadServletSync extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();

        long totalBytes = 0;
        try {
            ServletInputStream inputStream = request.getInputStream();
            int len;
            byte byteBuff[] = new byte[1024];
            while ((len = inputStream.read(byteBuff)) != -1) {
                // throw it away
                totalBytes += len;
            }
        } catch (EOFException e) {
            // during shutdown this can happen. we don't care
        } finally {
            response.getWriter().println("<title>File Upload Result</title>");
        }
    }

}
