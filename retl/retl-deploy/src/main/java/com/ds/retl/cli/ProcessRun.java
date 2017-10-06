package com.ds.retl.cli;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by john on 2017/9/15.
 */
public class ProcessRun {
    private static final Log logger = LogFactory.getLog(ProcessRun.class);

    public static String runCmd(List<String> cmd) {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        // 将Error合并到常规输出中
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                StringBuffer sb = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                return sb.toString();
            }
        } catch (IOException ex) {
            String message = String.format("Run command fail, cmd: %s.", StringUtils.merge(cmd, " "));
            if (logger.isErrorEnabled()) {
                logger.error(message, ex);
            }
            return message;
        }
    }
}
