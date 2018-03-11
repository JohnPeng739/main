package org.mx.comps.notify.processor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.comps.notify.processor.MessageProcessor;
import org.mx.comps.notify.processor.MessageProcessorChain;
import org.mx.spring.SpringContextHolder;

/**
 * 注销终端命令处理器，收到本命令后将终端从在线终端管理器中进行注销。<br>
 * 收到的注销指令数据格式为：
 * <pre>
 * {
 *     command: 'unregistry',
 *     type: 'system',
 *     data: {
 *         deviceId: '',
 *         state: '',
 *         lastTime: 0,
 *         lastLongitude: 0.0,
 *         lastLatitude: 0.0
 *     }
 * }
 * </pre>
 * 注销响应的数据格式为：
 * <pre>
 * {
 *     srcCommand: 'unregistry',
 *     deviceId: '',
 *     status: 'ok',
 *     error: ''
 * }
 * </pre>
 *
 * @author : john.peng created on date : 2018/1/5
 */
public class UnregistryCommandProcessor extends DeviceCommandProcessor {
    private static final Log logger = LogFactory.getLog(UnregistryCommandProcessor.class);

    public static final String COMMAND = "unregistry";

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
     * @see DeviceCommandProcessor#processCommand(String, String, OnlineDevice)
     */
    @Override
    protected boolean processCommand(String command, String type, OnlineDevice onlineDevice) {
        if (COMMAND.equals(command) && MessageProcessorChain.TYPE_SYSTEM.equals(type)) {
            // 注销指令
            OnlineManager onlineManager = SpringContextHolder.getBean(OnlineManager.class);
            if (onlineManager.unregistryDevice(onlineDevice)) {
                super.sendResponseMessage(onlineDevice.getConnectKey(), command, onlineDevice.getDeviceId(), null);
            } else {
                super.sendResponseMessage(onlineDevice.getConnectKey(), command, onlineDevice.getDeviceId(),
                        "Unregistry device fail.");
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Process %s command successfully, data: %s.", command,
                        JSON.toJSONString(onlineDevice)));
            }
            return true;
        } else {
            return false;
        }
    }
}
