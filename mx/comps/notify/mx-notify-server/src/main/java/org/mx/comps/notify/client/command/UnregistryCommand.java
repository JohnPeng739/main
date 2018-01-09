package org.mx.comps.notify.client.command;

import org.mx.comps.notify.processor.MessageProcessorChain;

/**
 * 注销命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class UnregistryCommand extends BaseCommand {
    private RegistryData data;

    public UnregistryCommand(String deviceId) {
        super("unregistry", MessageProcessorChain.TYPE_SYSTEM);
        this.data = new RegistryData(deviceId);
    }

    public void setData(RegistryData data) {
        this.data = data;
    }

    public RegistryData getData() {
        return data;
    }

    public class RegistryData {
        private String deviceId;

        public RegistryData(String deviceId) {
            super();
            this.deviceId = deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceId() {
            return deviceId;
        }
    }
}
