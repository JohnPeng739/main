package org.mx.comps.notify.client.command;

/**
 * 设备注册命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class RegistryCommand extends Command {
    private String messageId = "registry", messageVersion = "1.0";

    /**
     * 构造函数
     *
     * @param deviceId  设备号
     * @param state     设备状态
     * @param longitude 经度
     * @param latitude  纬度
     * @param extraData 扩展数据
     */
    public RegistryCommand(String deviceId, String state, double longitude, double latitude, String extraData) {
        super("registry", CommandType.SYSTEM);
        super.setMessage(new Message<>(messageId, messageVersion,
                new RegistryData(deviceId, state, longitude, latitude, extraData)));
    }

    /**
     * 设备注册数据对象定义
     */
    public class RegistryData {
        private String deviceId, state, extraData;
        private double longitude, latitude;

        /**
         * 构造函数
         *
         * @param deviceId  设备号
         * @param state     设备状态
         * @param longitude 经度
         * @param latitude  纬度
         * @param extraData 扩展数据
         */
        public RegistryData(String deviceId, String state, double longitude, double latitude, String extraData) {
            super();
            this.deviceId = deviceId;
            this.state = state;
            this.longitude = longitude;
            this.latitude = latitude;
            this.extraData = extraData;
        }

        /**
         * 获取设备号
         *
         * @return 设备号
         */
        public String getDeviceId() {
            return deviceId;
        }

        /**
         * 设置设备号
         *
         * @param deviceId 设备号
         */
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        /**
         * 获取设备状态
         *
         * @return 状态
         */
        public String getState() {
            return state;
        }

        /**
         * 设置设备状态
         *
         * @param state 状态
         */
        public void setState(String state) {
            this.state = state;
        }

        /**
         * 获取设备位置：经度
         *
         * @return 经度
         */
        public double getLongitude() {
            return longitude;
        }

        /**
         * 设置设备位置：经度
         *
         * @param longitude 经度
         */
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        /**
         * 获取设备位置：纬度
         *
         * @return 纬度
         */
        public double getLatitude() {
            return latitude;
        }

        /**
         * 设置设备位置：纬度
         *
         * @param latitude 纬度
         */
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        /**
         * 获取设备扩展数据
         *
         * @return 扩展数据
         */
        public String getExtraData() {
            return extraData;
        }

        /**
         * 设置设备扩展数据
         *
         * @param extraData 扩展数据
         */
        public void setExtraData(String extraData) {
            this.extraData = extraData;
        }
    }
}
