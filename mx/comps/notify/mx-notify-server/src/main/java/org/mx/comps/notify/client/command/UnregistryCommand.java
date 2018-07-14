package org.mx.comps.notify.client.command;

/**
 * 设备注销命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class UnregistryCommand extends Command {
    private String messageId = "unregistry", messageVersion = "1.0";

    /**
     * 构造函数
     *
     * @param deviceId 设备号
     */
    public UnregistryCommand(String deviceId) {
        super("unregistry", CommandType.SYSTEM);
        super.setMessage(new Message<>(messageId, messageVersion, new UnregistryData(deviceId)));
    }

    /**
     * 设备注销数据对象定义
     */
    public class UnregistryData {
        private String deviceId;

        /**
         * 构造函数
         *
         * @param deviceId 设备号
         */
        UnregistryData(String deviceId) {
            super();
            this.deviceId = deviceId;
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
    }
}
