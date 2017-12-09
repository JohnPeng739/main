package org.mx.comps.file.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.file.FileReadProcessor;
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
    public static final String FILE_DOWNLOAD_URI_PATH = "/download";
    public static final String FILE_ROOT = "file.root";
    private static final Log logger = LogFactory.getLog(FileDownloadServlet.class);
    @Autowired
    private Environment env = null;

    @Autowired
    private ApplicationContext context = null;

    private FileReadProcessor readProcessor = null;

    private String root;

    public FileDownloadServlet() {
        super(FILE_DOWNLOAD_URI_PATH);
    }

    private void initBean(HttpServletRequest req) {
        root = env.getProperty(FILE_ROOT, String.class, System.getProperty("user.dir"));
        String readProcessorType = req.getParameter("processorType");
        if (StringUtils.isBlank(readProcessorType)) {
            // 如果没有设置processorType参数，则使用simple类型。
            readProcessorType = "simple";
        }
        switch (readProcessorType) {
            case "simple":
                readProcessor = context.getBean("simpleFilePersistProcessor", FileReadProcessor.class);
                break;
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported file write processor type: %s.", readProcessorType));
                }
                break;
        }
        readProcessor.init(req);
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initBean(req);
        resp.reset();
        String filename = readProcessor.getFileServiceDescriptor().getFilename();
        long length = readProcessor.getFileServiceDescriptor().getLength();
        if (req.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
            filename = URLEncoder.encode(filename, "UTF-8");
        } else {
            filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
        }
        resp.addHeader("Content-Disposition", "attachment;filename=" + filename);
        resp.addHeader("Content-Length", "" + length);
        resp.setContentLength((int) length);
        OutputStream out = resp.getOutputStream();
        readProcessor.read(out);
        out.flush();
        readProcessor.close();
        out.close();
    }
}