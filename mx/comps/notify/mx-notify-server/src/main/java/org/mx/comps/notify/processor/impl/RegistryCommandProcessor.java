package org.mx.comps.notify.processor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.service.server.websocket.WsSessionManager;
import org.mx.spring.utils.SpringContextHolder;

/**
 * 注册终端命令处理器，收到本命令后，将设备注册到在线设备管理器中。<br>
 * 收到的注册指令数据格式为：
 * <pre>
 * {
 *     command: 'registry',
 *     type: 'system',
 *     payload: {
 *         deviceId: '',
 *         state: '',
 *         lastTime: 0,
 *         lastLongitude: 0.0,
 *         lastLatitude: 0.0
 *     }
 * }
 * </pre>
 * 注册响应的数据格式为：
 * <pre>
 * {
 *     srcCommand: 'registry',
 *     deviceId: '',
 *     status: 'ok',
 *     error: ''
 * }
 * </pre>
 *
 * @author : john.peng created on date : 2018/1/5
 */
public class RegistryCommandProcessor extends DeviceCommandProcessor {
    public static final String COMMAND = "registry";
    private static final Log logger = LogFactory.getLog(RegistryCommandProcessor.class);

    /**
     * 默认的构造函数
     */
    public RegistryCommandProcessor(WsSessionManager sessionManager) {
        super(COMMAND, sessionManager);
    }

    /**
     * {@inheritDoc}
     *
     * @see DeviceCommandProcessor#processCommand(String, String, OnlineDevice)
     */
    @Override
    protected boolean processCommand(String command, String type, OnlineDevice onlineDevice) {
        String connectKey = onlineDevice.getConnectKey();
        try {
            // 注册指令
            OnlineManager onlineManager = SpringContextHolder.getBean(OnlineManager.class);
            if (onlineManager.registryDevice(onlineDevice)) {
                super.sendResponseMessage(connectKey, command, onlineDevice.getDeviceId(), null);
            } else {
                super.sendResponseMessage(connectKey, command, onlineDevice.getDeviceId(), "Registry device fail.");
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Process registry command successfully for session[%s].", connectKey));
            }
            return true;
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Process registry command fail.", ex);
            }
            super.sendResponseMessage(connectKey, command, onlineDevice.getDeviceId(), ex.getMessage());
            return false;
        }
    }
}
