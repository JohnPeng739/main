package com.ds.retl.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/11/2.
 */
public class SystemCtlCli {
    private static final String SYSTEMCTL = "systemctl";

    private static String runCmd(String cmd, String serviceName) {
        List<String> cmds = new ArrayList<>();
        cmds.add(String.format("/usr/bin/%s", SYSTEMCTL));
        cmds.add(cmd);
        cmds.add(serviceName);
        return ProcessRun.runCmd(cmds);
    }

    public static String enable(String serviceName) {
        return runCmd("enable", serviceName);
    }

    public static String disable(String serviceName) {
        return runCmd("disable", serviceName);
    }

    public static String start(String serviceName) {
        return runCmd("start", serviceName);
    }

    public static String stop(String serviceName) {
        return runCmd("stop", serviceName);
    }

    public static String status(String serviceName) {
        return runCmd("status", serviceName);
    }

    public static String reload(String serviceName) {
        return runCmd("reload", serviceName);
    }

    public static String restart(String serviceName) {
        return runCmd("restart", serviceName);
    }
}
