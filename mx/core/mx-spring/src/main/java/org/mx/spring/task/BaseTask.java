package org.mx.spring.task;

/**
 * 描述： 默认的任务类
 *
 * @author john peng
 * Date time 2018/5/14 上午10:06
 */
public abstract class BaseTask implements Task {
    private String name = "default";
    private boolean async = true;
    private long startTime = -1, finishTime = -1;
    private TaskState state = TaskState.INIT;

    /**
     * 默认的构造函数
     */
    public BaseTask() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param name 任务名称
     */
    public BaseTask(String name) {
        this();
        this.name = name;
    }

    /**
     * 默认的构造函数
     *
     * @param name  任务名称
     * @param async 设置为true表示需要异步执行，否则为同步执行
     */
    public BaseTask(String name, boolean async) {
        this(name);
        this.async = async;
    }

    /**
     * {@inheritDoc}
     *
     * @see Task#getName()
     */
    @Override
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
     * {@inheritDoc}
     *
     * @see Task#isAsync()
     */
    @Override
    public boolean isAsync() {
        return async;
    }

    /**
     * 设置是否需要异步执行
     *
     * @param async 设置为true表示任务需要异步执行；否则为同步执行。
     */
    protected void setAsync(boolean async) {
        this.async = async;
    }

    /**
     * {@inheritDoc}
     *
     * @see Task#getState()
     */
    @Override
    public TaskState getState() {
        return state;
    }

    /**
     * 设置任务状态
     *
     * @param state 任务状态
     */
    protected void setState(TaskState state) {
        this.state = state;
    }

    /**
     * {@inheritDoc}
     *
     * @see Task#getStartTime()
     */
    @Override
    public long getStartTime() {
        return startTime;
    }

    /**
     * 设置执行开始时间，精确到毫秒
     *
     * @param startTime 开始时间
     */
    protected void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see Task#getFinishTime()
     */
    @Override
    public long getFinishTime() {
        return finishTime;
    }

    /**
     * 设置执行完成时间，精确到毫秒
     *
     * @param finishTime 完成时间
     */
    protected void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
}
