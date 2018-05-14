package org.mx.spring.task;

import java.util.concurrent.TimeUnit;

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

    /**
     * 任务调度配置信息
     */
    class ScheduledConfig {
        private boolean onlyOne = true;
        private long delay = 0, period = 1000;
        private TimeUnit timeUnit = TimeUnit.MICROSECONDS;

        /**
         * 默认的构造函数
         *
         * @param onlyOne  仅执行一次
         * @param delay    执行前延迟的时间
         * @param timeUnit 时间单位
         */
        public ScheduledConfig(boolean onlyOne, long delay, TimeUnit timeUnit) {
            super();
            this.onlyOne = onlyOne;
            this.delay = delay;
            this.timeUnit = timeUnit;
        }

        /**
         * 默认的构造函数
         *
         * @param onlyOne  仅执行一次
         * @param delay    执行前延迟的时间
         * @param period   两次执行之间间隔的时间
         * @param timeUnit 时间单位
         */
        public ScheduledConfig(boolean onlyOne, long delay, long period, TimeUnit timeUnit) {
            this(onlyOne, delay, timeUnit);
            this.period = period;
        }

        /**
         * 以毫秒为单位构建调度周期
         *
         * @param delay  执行前延迟的时间
         * @param period 两次执行之间间隔的时间
         * @return 任务调度配置信息
         */
        public static ScheduledConfig MS(long delay, long period) {
            return new ScheduledConfig(false, delay, period, TimeUnit.MICROSECONDS);
        }

        /**
         * 以秒为单位构建调度周期
         *
         * @param delay  执行前延迟的时间
         * @param period 两次执行之间间隔的时间
         * @return 任务调度配置信息
         */
        public static ScheduledConfig S(long delay, long period) {
            return new ScheduledConfig(false, delay, period, TimeUnit.SECONDS);
        }

        /**
         * 以分钟为单位构建调度周期
         *
         * @param delay  执行前延迟的时间
         * @param period 两次执行之间间隔的时间
         * @return 任务调度配置信息
         */
        public static ScheduledConfig M(long delay, long period) {
            return new ScheduledConfig(false, delay, period, TimeUnit.MINUTES);
        }

        /**
         * 以小时为单位构建调度周期
         *
         * @param delay  执行前延迟的时间
         * @param period 两次执行之间间隔的时间
         * @return 任务调度配置信息
         */
        public static ScheduledConfig H(long delay, long period) {
            return new ScheduledConfig(false, delay, period, TimeUnit.HOURS);
        }

        /**
         * 以天为单位构建调度周期
         *
         * @param delay  执行前延迟的时间
         * @param period 两次执行之间间隔的时间
         * @return 任务调度配置信息
         */
        public static ScheduledConfig D(long delay, long period) {
            return new ScheduledConfig(false, delay, period, TimeUnit.DAYS);
        }

        /**
         * 返回是否执行一次
         *
         * @return 返回true表示仅执行一次，否则为周期型执行
         */
        public boolean isOnlyOne() {
            return onlyOne;
        }

        /**
         * 设置是否仅执行一次
         *
         * @param onlyOne 设置为true表示仅执行一次，否则为周期性执行
         */
        public void setOnlyOne(boolean onlyOne) {
            this.onlyOne = onlyOne;
        }

        /**
         * 获取第一次执行需要延迟的时间
         *
         * @return 延迟的时间
         */
        public long getDelay() {
            return delay;
        }

        /**
         * 设置第一次执行前需要延迟的时间
         *
         * @param delay 延迟的时间
         */
        public void setDelay(long delay) {
            this.delay = delay;
        }

        /**
         * 获取两次执行之间的间隔时间
         *
         * @return 间隔时间
         */
        public long getPeriod() {
            return period;
        }

        /**
         * 设置两次执行之间的间隔时间
         *
         * @param period 间隔时间
         */
        public void setPeriod(long period) {
            this.period = period;
        }

        /**
         * 获取时间单位
         *
         * @return 时间单位
         */
        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        /**
         * 设置时间单位
         *
         * @param timeUnit 时间单位
         */
        public void setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }
    }
}
