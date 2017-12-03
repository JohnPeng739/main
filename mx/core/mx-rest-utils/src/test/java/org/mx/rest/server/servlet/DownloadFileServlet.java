package org.mx.rest.server.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 测试文件下载的Servlet服务类
 *
 * @author : john.peng created on date : 2017/12/03
 */
@Component
public class DownloadFileServlet extends BaseHttpServlet {
    private static final Log logger = LogFactory.getLog(DownloadFileServlet.class);

    public DownloadFileServlet() {
        super("/download");
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseHttpServlet#service(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        try {
            String filename = String.format("%s.txt", id);
            String msg = "This is a test message file.";
            int length = msg.length();
            response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            response.addHeader("Content-Length", "" + length);
            response.setContentLength(length);
            OutputStream out = response.getOutputStream();
            out.write(msg.getBytes());
            out.flush();
            out.close();
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Download [%s] fail.", id), ex);
            }
        }
    }
}
