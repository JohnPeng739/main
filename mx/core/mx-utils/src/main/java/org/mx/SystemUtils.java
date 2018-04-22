package org.mx;

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.error.UserInterfaceSystemErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.function.Function;

/**
 * 描述： 系统级参数应用类
 *
 * @author John.Peng
 *         Date time 2018/4/20 上午10:11
 */
public class SystemUtils {
    private static final Log logger = LogFactory.getLog(SystemUtils.class);

    private SystemUtils() {
        super();
    }

    /**
     * 获取操作系统类型
     *
     * @return 操作系统类型枚举
     * @see SystemType
     */
    public static SystemType getType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            return SystemType.Windows;
        } else if (osName.startsWith("linux")) {
            return SystemType.Linux;
        } else if (osName.startsWith("mac")) {
            return SystemType.Mac;
        } else {
            return SystemType.Other;
        }
    }

    /**
     * 获取当前JVM的进程ID
     *
     * @return 进程ID
     */
    public static String getJvmPid() {
        return getSystemStat(jvmPid());
    }

    /**
     * 获取当前JVM占用的CPU比率
     *
     * @param identity 当前JVM运行程序的标识
     * @return CPU比率
     */
    public static float getCpuRate(final String identity) {
        return getSystemStat(cpuRate(identity));
    }

    /**
     * 获取当前JVM占用的线程数
     *
     * @return 线程数
     */
    public static int getThreads() {
        return getSystemStat(threads());
    }

    /**
     * 获取当前JVM占用的内存比率
     *
     * @return 占用的内存比率
     */
    public static float getMemoryRate() {
        return getSystemStat(memory());
    }

    private static <Void, R> R getSystemStat(Function<Void, R> function) {
        return function.apply(null);
    }

    private static Function<Void, String> jvmPid() {
        return avoid -> ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    private static Function<Void, Float> cpuRate(final String identity) {
        SystemType type = getType();
        switch (type) {
            case Windows:
                return aVoid -> {
                    String command = String.format("%s\\system32\\wbem\\wmic.exe process " +
                            "get Caption,CommandLine,KernelModeTime,UserModeTime", System.getenv("windir"));
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Command: %s", command));
                    }
                    try {
                        Process process = Runtime.getRuntime().exec(new String[] {"CMD", "/C", command});
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        Object[] lines = bufferedReader.lines().toArray();
                        long myTime = 0, totalTime = 0;
                        if (lines.length > 0){
                            String line = (String)lines[0];
                            int[] pos = {line.indexOf("Caption"), line.indexOf("CommandLine"),
                                    line.indexOf("KernelModeTime"), line.indexOf("UserModeTime")};
                            for (int index = 1; index < lines.length; index ++) {
                                line = (String)lines[index];
                                if (StringUtils.isBlank(line)) {
                                    continue;
                                }
                                String caption = line.substring(pos[0], pos[1]).trim(),
                                        commandLine = line.substring(pos[1], pos[2]).trim();
                                long kernelTime = Long.valueOf(line.substring(pos[2], pos[3]).trim()),
                                        userTime = Long.valueOf(line.substring(pos[3]).trim());
                                totalTime += kernelTime + userTime;
                                if (caption.contains(identity) || commandLine.contains(identity)) {
                                    myTime = kernelTime + userTime;
                                }
                            }
                        }
                        if (totalTime <= 0) {
                            totalTime = 1;
                        }
                        return (float) myTime / totalTime;
                    } catch (IOException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Invoke command[%s] fail.", command), ex);
                        }
                        throw new UserInterfaceSystemErrorException(
                                UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE);
                    }
                };
            case Linux:
            case Mac:
                return avoid -> {
                    String command = String.format("ps ux -p %s | awk '{print $3}'", getSystemStat(jvmPid()));
                    try {
                        Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        bufferedReader.readLine();
                        String line = bufferedReader.readLine();
                        return Float.valueOf(line);
                    } catch (IOException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Invoke command[%s] fail.", command), ex);
                        }
                        throw new UserInterfaceSystemErrorException(
                                UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE);
                    }
                };
            default:
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED);
        }
    }

    private static Function<Void, Integer> threads() {
        return aVoid -> {
            ThreadGroup parentThread;
            for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                    .getParent() != null; ) {
                parentThread = parentThread.getParent();
            }
            return parentThread.activeCount();
        };
    }

    private static Function<Void, Float> memory() {
        SystemType type = getType();
        switch (type) {
            case Windows:
                return aVoid -> {
                    String command = String.format("TASKLIST /NH /FO CSV /FI \"PID EQ %s\"", getSystemStat(jvmPid()));
                    try {
                        Process process = Runtime.getRuntime().exec(command);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        List<String> segs = TypeUtils.csv2List(bufferedReader.readLine());
                        String memoryStr = segs.get(4).replaceAll("[, \"]", "");
                        long memory = StringUtils.string2Size(memoryStr, 0);
                        long total = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())
                                .getTotalPhysicalMemorySize();
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Memory fetch, seg: %s, memory: %d, total: %d.", segs.get(4), memory, total));
                        }
                        return (float) memory / total;
                    } catch (IOException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Invoke the command[%s] fail.", ex));
                        }
                        throw new UserInterfaceSystemErrorException(
                                UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE);
                    }
                };
            case Linux:
            case Mac:
                return aVoid -> {
                    String command = type == SystemType.Mac ?
                            String.format("ps u -p %s | awk '{print $4}'", getSystemStat(jvmPid())) :
                            String.format("ps u -p %s | awk '{print $3}'", getSystemStat(jvmPid()));
                    try {
                        Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        bufferedReader.readLine();
                        return Float.valueOf(bufferedReader.readLine());
                    } catch (IOException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Invoke the command[%s] fail", command), ex);
                        }
                        throw new UserInterfaceSystemErrorException(
                                UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE
                        );
                    }
                };
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported type: %s.", type));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED
                );
        }
    }

    /**
     * 操作系统枚举类型定义
     */
    public enum SystemType {
        /**
         * Windows操作系统
         */
        Windows,
        /**
         * Linux操作系统
         */
        Linux,
        /**
         * Mac操作系统
         */
        Mac,
        /**
         * 其他类型操作系统
         */
        Other
    }
}
