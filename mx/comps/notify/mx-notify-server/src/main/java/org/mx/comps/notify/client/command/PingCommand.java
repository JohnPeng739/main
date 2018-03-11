package org.mx.comps.notify.client.command;

import org.mx.comps.notify.processor.MessageProcessorChain;

/**
 * 心跳命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class PingCommand extends BaseCommand {
    private PongData data;

    public PingCommand(String deviceId, String state, double longitude, double latitude) {
        super("ping", MessageProcessorChain.TYPE_SYSTEM);
        this.data = new PongData(deviceId, state, longitude, latitude);
    }

    public PongData getData() {
        return data;
    }

    public void setData(PongData data) {
        this.data = data;
    }

    public class PongData {
        private String deviceId, state;
        private double longitude, latitude;

        public PongData(String deviceId, String state, double longitude, double latitude) {
            super();
            this.deviceId = deviceId;
            this.state = state;
            this.longitude = longitude;
            this.latitude = latitude;
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
    }
}
