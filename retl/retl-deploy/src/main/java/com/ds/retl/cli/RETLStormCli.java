package com.ds.retl.cli;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/9/15.
 */
public class RETLStormCli {
    private static final Log logger = LogFactory.getLog(RETLStormCli.class);

    private String stormHome = "/opt/storm";
    private String storm = "storm";
    private String retlJar = "retlp.jar";
    private String deps = "deps";

    public void setStormHome(String stormHome) {
        this.stormHome = stormHome;
    }

    public void setStorm(String storm) {
        this.storm = storm;
    }

    public void setRetlJar(String retlJar) {
        this.retlJar = retlJar;
    }

    public void setDeps(String deps) {
        this.deps = deps;
    }

    public String deploy(String topologyConfigJsonFile) {
        List<String> cmds = new ArrayList<>();
        cmds.add(String.format("%s/bin/%s", stormHome, storm));
        cmds.add("jar");
        cmds.add(String.format("%s/retl/%s", stormHome, retlJar));
        cmds.add("com.ds.retl.ETLTopologyBuilder");
        cmds.add(topologyConfigJsonFile);
        String depsPath = String.format("%s/retl/%s", deps);
        List<String> depJars = new ArrayList<>();
        findDeps(depJars, new File(depsPath));
        if (!depJars.isEmpty()) {
            cmds.add("--jars");
            cmds.add(StringUtils.merge(depJars, ","));
        }
        return ProcessRun.runCmd(cmds);
    }

    private void findDeps(List<String> deps, File path) {
        if (path == null) {
            return;
        }
        if (path.isDirectory()) {
            File[] children = path.listFiles();
            for (File child : children) {
                findDeps(deps, child);
            }
        } else if (path.isFile()) {
            String p = path.getAbsolutePath();
            if (p.endsWith(".jar") || p.endsWith(".zip")) {
                deps.add(p);
            }
        }
    }
}
