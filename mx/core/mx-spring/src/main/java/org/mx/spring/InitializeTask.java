package org.mx.spring;

import org.mx.spring.task.BaseTask;

/**
 * 初始化任务抽象类
 *
 * @author : john.peng created on date : 2018/1/17
 * @deprecated 在未来版本将被 {@link BaseTask} 取代。
 */
public abstract class InitializeTask extends BaseTask {
    /**
     * 默认的构造函数
     *
     * @param name         任务名称
     * @param longTimeTask 是否长时间任务，长时间任务将在独立的线程中执行。
     */
    public InitializeTask(String name, boolean longTimeTask) {
        super(name, longTimeTask);
    }

    /**
     * 获取是否长时间任务，长时间任务将在独立线程中执行。
     *
     * @return 长时间任务返回true，否则返回false
     */
    public boolean isLongTimeTask() {
        return super.isAsync();
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseTask#invoke()
     */
    @Override
    public void invoke() {
        this.invokeTask();
    }

    /**
     * 执行任务
     */
    public abstract void invokeTask();
}
