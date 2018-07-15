package org.mx.comps.notify.client.command;

/**
 * 描述： 推送服务器中使用的指令对象定义
 *
 * @author john peng
 * Date time 2018/7/14 上午11:00
 */
public class Command<T> {
    private String command;
    private CommandType type = CommandType.SYSTEM;
    private T payload = null;

    /**
     * 默认的构造函数
     */
    public Command() {
        super();
    }

    /**
     * 构造函数
     *
     * @param command 命令
     * @param type    命令类型
     */
    public Command(String command, CommandType type) {
        this();
        this.command = command;
        this.type = type;
    }

    /**
     * 获取命令
     *
     * @return 命令
     */
    public String getCommand() {
        return command;
    }

    /**
     * 设置命令
     *
     * @param command 命令
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * 获取命令类型
     *
     * @return 命令类型
     */
    public CommandType getType() {
        return type;
    }

    /**
     * 设置命令类型
     *
     * @param type 命令类型
     * @see CommandType
     */
    public void setType(CommandType type) {
        this.type = type;
    }

    /**
     * 获取命令对象中的消息对象
     *
     * @return 消息对象
     */
    public T getPayload() {
        return payload;
    }

    /**
     * 设置命令对象中的消息对象
     *
     * @param payload 消息对象
     */
    public void setPayload(T payload) {
        this.payload = payload;
    }

    /**
     * 命令类型枚举定义
     */
    public enum CommandType {
        SYSTEM, USER
    }
}
