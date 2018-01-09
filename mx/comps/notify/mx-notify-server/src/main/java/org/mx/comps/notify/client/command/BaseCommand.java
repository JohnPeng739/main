package org.mx.comps.notify.client.command;

/**
 * 命令基类
 *
 * @author : john.peng created on date : 2018/1/9
 */
public abstract class BaseCommand {
    private String command, type;

    public BaseCommand(String command, String type) {
        super();
        this.command = command;
        this.type = type;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public String getType() {
        return type;
    }
}
