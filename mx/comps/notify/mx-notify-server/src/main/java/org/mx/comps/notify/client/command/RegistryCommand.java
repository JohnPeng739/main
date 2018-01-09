package org.mx.comps.notify.client.command;

import org.mx.comps.notify.processor.MessageProcessorChain;

/**
 * 注册命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class RegistryCommand extends BaseCommand {
    private RegistryData data;

    public RegistryCommand(String deviceId, String state, double longitude, double latitude) {
        super("registry", MessageProcessorChain.TYPE_SYSTEM);
        this.data = new RegistryData(deviceId, state, longitude, latitude);
    }

    public void setData(RegistryData data) {
        this.data = data;
    }

    public RegistryData getData() {
        return data;
    }

    public class RegistryData {
        private String deviceId, state;
        private double longitude, latitude;

        public RegistryData(String deviceId, String state, double longitude, double latitude) {
            super();
            this.deviceId = deviceId;
            this.state = state;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getState() {
            return state;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }
}
