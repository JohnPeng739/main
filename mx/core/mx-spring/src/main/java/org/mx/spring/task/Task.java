package org.mx.spring.task;

/**
 * 描述： 可执行的任务接口定义
 *
 * @author john peng
 * Date time 2018/5/14 上午9:47
 */
public interface Task {
    /**
     * 获取任务名称
     *
     * @return 任务名称
     */
    String getName();

    /**
     * 获取任务是否需要异步执行
     *
     * @return 如果返回true，表示异步执行；否则返回false。
     */
    boolean isAsync();

    /**
     * 获取任务当前状态
     *
     * @return 状态枚举
     */
    TaskState getState();

    /**
     * 获取执行开始时间，精确到毫秒
     *
     * @return 开始时间
     */
    long getStartTime();

    /**
     * 获取完成任务时间，精确到毫秒
     *
     * @return 完成时间
     */
    long getFinishTime();

    /**
     * 调用执行一次任务
     */
    void invoke();

    /**
     * 任务状态枚举
     */
    enum TaskState {
        INIT, RUNNING, FINISHED, ERROR
    }

    class ScheduledConfig {
        // TODO 定义周期性执行参数
    }
}
