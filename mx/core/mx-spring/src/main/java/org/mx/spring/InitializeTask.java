package org.mx.spring;

/**
 * 初始化任务抽象类
 *
 * @author : john.peng created on date : 2018/1/17
 */
public abstract class InitializeTask {
    private String name = "default";
    private boolean longTimeTask = false;

    /**
     * 默认的构造函数
     */
    public InitializeTask() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param name         任务名称
     * @param longTimeTask 是否长时间任务，长时间任务将在独立的线程中执行。
     */
    public InitializeTask(String name, boolean longTimeTask) {
        this();
        this.name = name;
        this.longTimeTask = longTimeTask;
    }

    /**
     * 获取是否长时间任务，长时间任务将在独立线程中执行。
     *
     * @return 长时间任务返回true，否则返回false
     */
    public boolean isLongTimeTask() {
        return longTimeTask;
    }

    /**
     * 获取任务名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 执行任务
     */
    public abstract void invokeTask();
}
