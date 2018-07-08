package org.mx.comps.notify.client.command;

import org.mx.comps.notify.processor.MessageProcessorChain;

/**
 * 注册命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class RegistryCommand extends BaseCommand {
    private RegistryData data;

    public RegistryCommand(String deviceId, String state, double longitude, double latitude, String extraData) {
        super("registry", MessageProcessorChain.TYPE_SYSTEM);
        this.data = new RegistryData(deviceId, state, longitude, latitude, extraData);
    }

    public RegistryData getData() {
        return data;
    }

    public void setData(RegistryData data) {
        this.data = data;
    }

    public class RegistryData {
        private String deviceId, state, extraData;
        private double longitude, latitude;

        public RegistryData(String deviceId, String state, double longitude, double latitude, String extraData) {
            super();
            this.deviceId = deviceId;
            this.state = state;
            this.longitude = longitude;
            this.latitude = latitude;
            this.extraData = extraData;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getExtraData() {
            return extraData;
        }

        public void setExtraData(String extraData) {
            this.extraData = extraData;
        }
    }
}
