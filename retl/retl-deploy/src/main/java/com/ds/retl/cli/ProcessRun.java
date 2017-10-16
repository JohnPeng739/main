package com.ds.retl.cli;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * 采用ProcessBuilder封装的运行系统命令的工具类。
 * <p>
 * 特别的，对于使用管道命令连接的多条命令，必须采用特殊的用法，将整条命令作为一个参数进行传递，比如：
 * commands: "sh", "-c", "cat dependencies | grep 'config'"
 *
 * @author : john.peng created on date : 2017/9/15
 */
public class ProcessRun {
    private static final Log logger = LogFactory.getLog(ProcessRun.class);

    /**
     * 运行指定的系统命令
     *
     * @param cmds 命令参数列表，比如： "cat", "dependencies.gradle"
     * @return 命令运行完毕后的输出数据或者错误信息
     * @see #runCmd(List)
     */
    public static String runCmd(String... cmds) {
        return ProcessRun.runCmd(Arrays.asList(cmds));
    }

    /**
     * 运行指定的命令
     *
     * @param cmds 命令参数列表
     * @return 命令运行完毕后输出的数据或者错误信息
     */
    public static String runCmd(List<String> cmds) {
        ProcessBuilder builder = new ProcessBuilder(cmds);
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
            String message = String.format("Run command fail, cmd: %s.", StringUtils.merge(cmds, " "));
            if (logger.isErrorEnabled()) {
                logger.error(message, ex);
            }
            return message;
        }
    }

    /**
     * 运行指定的系统命令
     *
     * @param listener 命令运行过程中输出的数据和错误信息的接收接口
     * @param cmds     命令参数列表
     * @see #runCmd(OutputListener, List)
     */
    public static void runCmd(OutputListener listener, String... cmds) {
        ProcessRun.runCmd(listener, Arrays.asList(cmds));
    }

    /**
     * 运行指定的系统命令
     *
     * @param listener 命令运行过程中输出的数据和错误信息的接收接口
     * @param cmds     命令参数列表
     */
    public static void runCmd(OutputListener listener, List<String> cmds) {
        ProcessBuilder builder = new ProcessBuilder(cmds);
        // 将Error合并到常规输出中
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (listener != null) {
                        listener.receiveLine(line);
                    }
                }
            }
        } catch (IOException ex) {
            String message = String.format("Run command fail, cmd: %s.", StringUtils.merge(cmds, " "));
            if (logger.isErrorEnabled()) {
                logger.error(message, ex);
            }
        }
    }

    /**
     * 定义命令输出数据的接口定义，一般用于长时间输出数据的类型。
     */
    public interface OutputListener {
        /**
         * 命令输出一行数据时触发
         *
         * @param line 输出的一行数据
         */
        void receiveLine(String line);
    }
}
