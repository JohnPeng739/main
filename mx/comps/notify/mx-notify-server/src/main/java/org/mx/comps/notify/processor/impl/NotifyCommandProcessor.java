package org.mx.comps.notify.processor.impl;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.TypeUtils;
import org.mx.comps.notify.processor.MessageProcessor;
import org.mx.comps.notify.processor.NotifyProcessor;
import org.mx.comps.notify.client.command.Command;
import org.mx.spring.utils.SpringContextHolder;

/**
 * 通知命令处理器，收到本命令后，将命令中的数据直接P2P地发送到指定范围的注册设备上。<br>
 * 收到的指令格式为：
 * <pre>
 * {
 *     commond: 'notify',
 *     type: 'user',
 *     data: {
 *         src: '',
 *         deviceId: '',
 *         tarType: '',
 *         tar: '',
 *         expiredTime: -1,
 *         message: {
 *             ......
 *         }
 *     }
 * }
 * </pre>
 * 其中：tarType可以是 devices | ips | states | later | early ，
 * 分别对于tar中的设备号列表、IP列表、状态列表、晚于/早于时间(long)内容。<br>
 * 处理后封装的发送数据格式为：
 * <pre>
 * {
 *     src: '',
 *     deviceId: '',
 *     pushTime: 0,
 *     message: {
 *         ......
 *     }
 * }
 * </pre>
 * 响应给发送终端的数据格式为：
 * <pre>
 * {
 *     srcCommand: 'notify',
 *     deviceId: '',
 *     status: 'ok',
 *     error: ''
 * }
 * </pre>
 *
 * @author : john.peng created on date : 2018/1/6
 */
public class NotifyCommandProcessor implements MessageProcessor {
    public static final String COMMAND = "notify";

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#getCommand()
     */
    @Override
    public String getCommand() {
        return COMMAND;
    }

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#processJsonCommand(Session, JSONObject)
     */
    @Override
    public boolean processJsonCommand(Session session, JSONObject json) {
        String command = json.getString("command");
        String type = json.getString("type");
        if (COMMAND.equals(command) && Command.CommandType.USER.name().equalsIgnoreCase(type)) {
            // 通知消息
            JSONObject message = json.getJSONObject("message");
            JSONObject data = message.getJSONObject("data");
            String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
            int port = session.getRemoteAddress().getPort();
            data.put("connectKey", String.format("%s:%d", ip, port));
            NotifyProcessor notifyProcessor = SpringContextHolder.getBean(NotifyProcessor.class);
            notifyProcessor.notifyProcess(data);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#processBinaryData(Session, byte[])
     */
    @Override
    public boolean processBinaryData(Session session, byte[] buffer) {
        return false;
    }
}
