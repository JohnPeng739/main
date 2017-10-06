package com.ds.retl.zookeeper;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.*;
import org.mx.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 2017/9/17.
 */
public class ZookeeperOperate implements Watcher {
    private static final Log logger = LogFactory.getLog(ZookeeperOperate.class);

    private static ZookeeperOperate operate = null;

    private String serverList = null;
    private ZooKeeper zookeeper = null;

    private ZookeeperOperate() {
        super();
    }

    public static ZookeeperOperate getOperate() {
        if (operate == null) {
            operate = new ZookeeperOperate();
        }
        return operate;
    }

    @Override
    public void process(WatchedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Watch state: %s.", event.getState().name()));
        }
    }

    public void initialize(String serverList) {
        this.serverList = serverList;
        if (StringUtils.isBlank(serverList)) {
            throw new IllegalArgumentException("The zookeeper server list is blank.");
        }
        try {
            if (this.zookeeper != null) {
                // 如果已经初始化，则跳出
                return;
            }
            this.zookeeper = new ZooKeeper(serverList, 40 * 1000, this);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Connect the zookeeper server fail. servers: %s", serverList), ex);
            }
        }
    }

    public void initialize(JSONObject zookeeperConfig) {
        serverList = zookeeperConfig.getString("serverList");
        initialize(serverList);
    }

    private void reconnect() {
        try {
            if (this.zookeeper != null) {
                // 如果已经初始化，则先关闭
                this.zookeeper.close();
                this.zookeeper = null;
            }
            this.zookeeper = new ZooKeeper(serverList, 40 * 1000, this);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Reconnect zookeeper server successfully, servers: %s", serverList));
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Reconnect the zookeeper server fail.", serverList), ex);
            }
        }
    }

    public String getData(String path) {
        try {
            if (zookeeper.exists(path, false) != null) {
                return new String(zookeeper.getData(path, false, null), "UTF-8");
            }
        } catch (KeeperException.SessionExpiredException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is expired, will reconnect...");
            }
            reconnect();
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Get data fail from the path[%s].", path));
            }
        }
        return "";
    }

    public List<String> getChild(String path) {
        try {
            return zookeeper.getChildren(path, true);
        } catch (KeeperException.SessionExpiredException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is expired, will reconnect...");
            }
            reconnect();
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public Map<String, String> getChildData(String path) {
        try {
            Map<String, String> result = new HashMap<>();
            List<String> list = zookeeper.getChildren(path, true);
            for (String key : list) {
                String data = getData(String.format("%s/%s", path, key));
                if (data != null) {
                    result.put(key, data);
                }
            }
            return result;
        } catch (KeeperException.SessionExpiredException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is expired, will reconnect...");
            }
            reconnect();
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Get children fail from the path[%s].", path));
            }
        }
        return null;
    }

    public boolean exist(String path) {
        try {
            return zookeeper.exists(path, true) != null;
        } catch (KeeperException.SessionExpiredException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is expired, will reconnect...");
            }
            reconnect();
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public void createNode(String path, String defaultData) {
        if (exist(path)) {
            return;
        }
        try {
            int pos = path.lastIndexOf('/');
            if (pos > 0) {
                if (exist(path)) {
                    return;
                } else {
                    String parent = path.substring(0, pos);
                    createNode(parent, defaultData);
                    zookeeper.create(path, defaultData.getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } else {
                zookeeper.create(path, defaultData.getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException.SessionExpiredException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is expired, will reconnect...");
            }
            reconnect();
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Create node fail, path: %s, data: %s.", path, defaultData), ex);
            }
        }
    }

    public void setData(String path, String data) {
        try {
            if (!exist(path)) {
                createNode(path, "");
            }
            zookeeper.setData(path, data.getBytes("UTF-8"), -1);
        } catch (KeeperException.SessionExpiredException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is expired, will reconnect...");
            }
            reconnect();
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Set data[%s] fail from the path[%s].", data, path), ex);
            }
        }
    }

    public void delete(String path) {
        if (!exist(path)) {
            return;
        }
        try {
            List<String> children = zookeeper.getChildren(path, true);
            for (String key : children) {
                delete(String.format("%s/%s", path, key));
            }
            zookeeper.delete(path, -1);
        } catch (KeeperException.SessionExpiredException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is expired, will reconnect...");
            }
            reconnect();
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Delete node fail, path: %s.", path), ex);
            }
        }
    }

    public void close() throws InterruptedException {
        if (logger.isDebugEnabled()) {
            logger.debug("Will close the zookeeper client...");
        }
        if (zookeeper != null) {
            zookeeper.close();
            zookeeper = null;
        }
    }
}
