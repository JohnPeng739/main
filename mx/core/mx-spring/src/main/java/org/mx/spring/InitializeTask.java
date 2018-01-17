package org.mx.spring;

import org.springframework.context.ApplicationContext;

/**
 * 初始化任务抽象类
 *
 * @author : john.peng created on date : 2018/1/17
 */
public abstract class InitializeTask {
    private String name = "default";
    private boolean longTimeTask = false;
    private ApplicationContext context = null;

    /**
     * 获取是否长时间任务，长时间任务将在独立线程中执行。
     *
     * @return 长时间任务返回true，否则返回false
     */
    public boolean isLongTimeTask() {
        return longTimeTask;
    }

    /**
     * 设置是否长时间任务，长时间任务将在独立线程中执行。
     *
     * @param longTimeTask 长时间任务设置为true，否则设置为false
     */
    protected void setLongTimeTask(boolean longTimeTask) {
        this.longTimeTask = longTimeTask;
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
     * 设置任务名称
     *
     * @param name 名称
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * 设置Spring IoC上下文
     *
     * @param context 上下文
     */
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * 执行任务
     */
    public abstract void invokeTask();
}
