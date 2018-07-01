package org.mx.comps.notify.processor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.comps.notify.processor.MessageProcessor;
import org.mx.comps.notify.processor.MessageProcessorChain;
import org.mx.spring.utils.SpringContextHolder;

/**
 * 终端心跳命令处理器，收到本命令后，系统将更新终端信息，并回复终端响应信息。<br>
 * 收到的心跳指令格式如下：
 * <pre>
 *     {
 *         command: 'ping',
 *         type: 'system',
 *         data: {
 *             deviceId: '',
 *             state: '',
 *             lastTime: 0,
 *             lastLongitude: 0.0,
 *             lastLatitude: 0.0
 *         }
 *     }
 * </pre>
 * 心跳响应的数据格式如下：
 * <pre>
 *     {
 *         srcCommand: 'ping',
 *         deviceId: '',
 *         status: 'ok'
 *     }
 * </pre>
 *
 * @author : john.peng created on date : 2018/1/5
 */
public class PingCommandProcessor extends DeviceCommandProcessor {
    public static final String COMMAND = "ping";
    private static final Log logger = LogFactory.getLog(PingCommandProcessor.class);

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
            // 心跳指令
            OnlineManager onlineManager = SpringContextHolder.getBean(OnlineManager.class);
            if (onlineManager == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The OnlineManager is not initialized.");
                }
                return false;
            }
            onlineManager.pingDevice(onlineDevice);
            String connectKey = onlineDevice.getConnectKey();
            super.sendResponseMessage(connectKey, command, onlineDevice.getDeviceId(), null);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Process ping command successfully for session[%s].", connectKey));
            }
            return true;
        } else {
            return false;
        }
    }
}
