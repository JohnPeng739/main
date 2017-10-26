package com.ds.retl.cli;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 实时ETL命令行部署工具类，支持采用命令行方式部署计算拓扑到集群。
 *
 * @author : john.peng created on date : 2017/9/15
 */
public class RETLStormCli {
    private static final Log logger = LogFactory.getLog(RETLStormCli.class);

    private String stormHome = "/opt/storm";
    private String storm = "storm";
    private String retlHome = "/opt/retl";
    private String retlJar = "retl-platform-3.0.1.jar";
    private String deps = "deps";

    /**
     * 默认的构造函数
     */
    public RETLStormCli() {
        super();
    }

    /**
     * 构造函数
     *
     * @param stormHome    Storm的根目录
     * @param stormBin     Storm运行程序
     * @param retlHome     RETL的根目录
     * @param retlPlatform RETL平台Jar程序
     * @param retlDeps     RETL平台运行依赖程序目录
     */
    public RETLStormCli(String stormHome, String stormBin, String retlHome, String retlPlatform, String retlDeps) {
        this();
        this.stormHome = stormHome;
        this.storm = stormBin;
        this.retlHome = retlHome;
        this.retlJar = retlPlatform;
        this.deps = retlDeps;
    }

    /**
     * 设置Storm根目录
     *
     * @param stormHome Storm根目录
     */
    public void setStormHome(String stormHome) {
        this.stormHome = stormHome;
    }

    /**
     * 设置Storm运行程序
     *
     * @param storm Storm运行程序
     */
    public void setStorm(String storm) {
        this.storm = storm;
    }

    /**
     * 设置RETL根目录
     *
     * @param retlHome RETL根目录
     */
    public void setRetlHome(String retlHome) {
        this.retlHome = retlHome;
    }

    /**
     * 设置RETL平台运行JAR程序
     *
     * @param retlJar RETL平台运行JAR程序
     */
    public void setRetlJar(String retlJar) {
        this.retlJar = retlJar;
    }

    /**
     * 设置RETL平台运行依赖JAR目录
     *
     * @param deps 平台运行依赖目录
     */
    public void setDeps(String deps) {
        this.deps = deps;
    }

    /**
     * 部署指定RETL拓扑到集群
     *
     * @param topologyConfigJsonFile 拓扑配置文件路径
     * @return 部署过程中的命令行输出信息
     */
    public String deploy(String topologyConfigJsonFile) {
        List<String> cmds = new ArrayList<>();
        cmds.add(String.format("%s/bin/%s", stormHome, storm));
        cmds.add("jar");
        cmds.add(String.format("%s/%s", retlHome, retlJar));
        cmds.add("com.ds.retl.ETLTopologyBuilder");
        cmds.add(topologyConfigJsonFile);
        String depsPath = String.format("%s/%s", retlHome, deps);
        List<String> depJars = new ArrayList<>();
        findDeps(depJars, new File(depsPath));
        if (!depJars.isEmpty()) {
            cmds.add("--jars");
            cmds.add(StringUtils.merge(depJars, ","));
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("*****************\nStart a inline process:\n%s\n***********************",
                    StringUtils.merge(cmds, " ")));
        }
        return ProcessRun.runCmd(cmds, "提交拓扑成功");
    }

    /**
     * 查找RETL平台运行依赖文件
     *
     * @param deps 找到的依赖文件列表
     * @param path 依赖目录
     */
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
