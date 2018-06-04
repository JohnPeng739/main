package org.mx.service.client.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.service.server.comm.CommServiceProvider;

/**
 * 描述： 基于TCP/UDP通信的客户端调用程序，支持发送和接收操作。
 *
 * @author john peng
 * Date time 2018/6/4 上午11:49
 */
public class CommClientInvoke {
    private static final Log logger = LogFactory.getLog(CommClientInvoke.class);

    public CommClientInvoke() {
        this(CommServiceProvider.CommServiceType.UDP);
    }

    public CommClientInvoke(CommServiceProvider.CommServiceType type) {
        super();
        initClient(type);
    }

    private void initClient(CommServiceProvider.CommServiceType type) {
        // TODO
    }
}
