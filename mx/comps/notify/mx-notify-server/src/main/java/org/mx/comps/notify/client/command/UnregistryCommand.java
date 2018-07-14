package org.mx.comps.notify.client.command;

import org.mx.comps.notify.processor.MessageProcessorChain;

/**
 * 注销命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class UnregistryCommand extends BaseCommand {
    private UnregistryData data;

    public UnregistryCommand(String deviceId) {
        super("unregistry", MessageProcessorChain.TYPE_SYSTEM);
        this.data = new UnregistryData(deviceId);
    }

    public UnregistryData getData() {
        return data;
    }

    public void setData(UnregistryData data) {
        this.data = data;
    }

    public class UnregistryData {
        private String deviceId;

        UnregistryData(String deviceId) {
            super();
            this.deviceId = deviceId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }
    }
}
