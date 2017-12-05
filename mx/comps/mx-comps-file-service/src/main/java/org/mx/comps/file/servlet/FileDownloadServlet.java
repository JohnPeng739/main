package org.mx.comps.file.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.file.FilePersistProcessor;
import org.mx.service.server.servlet.BaseHttpServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 基于HttpServlet实现的文件下载实现类。
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Component("fileDownloadServlet")
public class FileDownloadServlet extends BaseHttpServlet {
    private static final Log logger = LogFactory.getLog(FileDownloadServlet.class);
    public static final String FILE_DOWNLOAD_URI_PATH = "/download";
    public static final String FILE_ROOT = "file.root";

    @Autowired
    private Environment env = null;

    @Autowired
    private ApplicationContext context = null;

    private FilePersistProcessor persistProcessor = null;

    private String root;

    public FileDownloadServlet() {
        super(FILE_DOWNLOAD_URI_PATH);
    }

    private void initBean(){
        root = env.getProperty(FILE_ROOT, String.class, System.getProperty("user.dir"));
        String persistType = env.getProperty("file.processor", String.class, "simple");
        switch (persistType) {
            case "simple":
                persistProcessor = context.getBean("simpleFilePersistProcessor", FilePersistProcessor.class);
                break;
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported file persist processor type: %s.", persistType));
                }
                break;
        }
    }

    /**
     * {@inheritDoc}
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initBean();
        persistProcessor.init(req);
        resp.reset();
        String filename = persistProcessor.getFilename();
        long length = persistProcessor.getLength();
        if (req.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
            filename = URLEncoder.encode(filename, "UTF-8");
        } else {
            filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
        }
        resp.addHeader("Content-Disposition", "attachment;filename=" + filename);
        resp.addHeader("Content-Length", "" + length);
        resp.setContentLength((int)length);
        OutputStream out = resp.getOutputStream();
        persistProcessor.read(out);
        out.flush();
        persistProcessor.close();
        out.close();
    }
}