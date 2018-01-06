package org.mx.comps.notify.processor.impl;

import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.comps.notify.processor.MessageProcessor;
import org.mx.comps.notify.processor.MessageProcessorChain;
import org.mx.spring.SpringContextHolder;

/**
 * 注册终端命令处理器
 *
 * @author : john.peng created on date : 2018/1/5
 */
public class PongCommandProcessor extends DeviceCommandProcessor {
    public static final String COMMAND = "pong";

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
            onlineManager.pongDevice(onlineDevice);
            return true;
        } else {
            return false;
        }
    }
}
