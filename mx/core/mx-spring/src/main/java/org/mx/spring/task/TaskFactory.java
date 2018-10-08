package org.mx.spring.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DateUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 描述： 创建任务执行工厂
 *
 * @author john peng
 * Date time 2018/5/14 上午10:15
 */
public class TaskFactory {
    private static final Log logger = LogFactory.getLog(TaskFactory.class);

    private ExecutorService singleExecutorService; // 串行执行服务
    private ExecutorService asyncExecutorPoolService; // 单次异步执行服务池
    private ScheduledExecutorService scheduledExecutorService; // 周期调度服务池

    private Map<String, Task> serialTasks = new ConcurrentHashMap<>();
    private Map<String, Task> asyncTasks = new ConcurrentHashMap<>();
    private Map<String, Task> scheduledTasks = new ConcurrentHashMap<>();
    private Map<String, Task.ScheduledConfig> scheduledConfigs = new HashMap<>();

    /**
     * 默认的构造函数
     */
    public TaskFactory() {
        super();
        singleExecutorService = Executors.newSingleThreadExecutor();
        asyncExecutorPoolService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 20,
                TimeUnit.SECONDS, new SynchronousQueue<>());
        scheduledExecutorService = Executors.newScheduledThreadPool(10);
    }

    /**
     * 获取所有的串行任务
     *
     * @return 串行任务列表
     */
    public Collection<Task> getSerialTasks() {
        return serialTasks.values();
    }

    /**
     * 获取所有的单次异步任务
     *
     * @return 单次异步任务列表
     */
    public Collection<Task> getAsyncTasks() {
        return asyncTasks.values();
    }

    /**
     * 获取指定的单次异步任务
     *
     * @param taskName 任务名称
     * @return 指定的任务对象，如果不存在，则返回null。
     */
    public Task getAsyncTask(String taskName) {
        return asyncTasks.get(taskName);
    }

    public Task removeAsyncTask(String taskName) {
        if (asyncTasks.containsKey(taskName)) {
            Task task = asyncTasks.get(taskName);
            cancelTask(task);
        }
        return asyncTasks.remove(taskName);
    }

    /**
     * 获取所有的可调度任务
     *
     * @return 可调度任务列表
     */
    public Collection<Task> getScheduledTasks() {
        return scheduledTasks.values();
    }

    /**
     * 获取指定的调度任务
     *
     * @param taskName 任务名称
     * @return 指定的任务对象，如果不存在，则返回null。
     */
    public Task getScheduledTask(String taskName) {
        return scheduledTasks.get(taskName);
    }

    public Task removeScheduledTask(String taskName) {
        if (scheduledTasks.containsKey(taskName)) {
            Task task = scheduledTasks.get(taskName);
            cancelTask(task);
        }
        return scheduledTasks.remove(taskName);
    }

    /**
     * 添加一个串行执行任务
     *
     * @param task 任务
     * @return 当前的任务工厂
     */
    public TaskFactory addSerialTask(Task task) {
        if (task.isAsync()) {
            if (logger.isWarnEnabled()) {
                logger.warn("You add a serial task, but the task is tagged async.");
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Submit a serial task.");
        }
        serialTasks.put(task.getName(), task);
        return this;
    }

    /**
     * 开始运行所有加入的串行任务
     */
    public void runSerialTasks() {
        if (serialTasks.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("There has not any serial tasks.");
            }
            return;
        }
        try {
            singleExecutorService.submit(() -> serialTasks.forEach((k, v) -> runTask(v)));
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Submit a serial task successfully, total: %d.", serialTasks.size()));
            }
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Submit a serial task fail.", ex);
            }
        }
    }

    /**
     * 添加一个异步执行任务
     *
     * @param task 任务
     * @return 当前的任务工厂
     */
    public TaskFactory addSingleAsyncTask(Task task) {
        if (logger.isInfoEnabled()) {
            logger.info("Submit a async task.");
        }
        asyncTasks.put(task.getName(), task);
        try {
            Future<?> future = asyncExecutorPoolService.submit(() -> runTask(task));
            if (task instanceof BaseTask) {
                ((BaseTask) task).setFuture(future);
            }
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Submit a async task successfully, name: %s.", task.getName()));
            }
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Submit a async task fail.", ex);
            }
        }
        return this;
    }

    /**
     * 添加一个可以被调度执行的任务，例如：设定延迟执行、周期性执行
     *
     * @param task   任务
     * @param config 调度配置信息
     * @return 当前的任务工厂
     * @see Task.ScheduledConfig
     */
    public TaskFactory addScheduledTask(Task task, Task.ScheduledConfig config) {
        if (logger.isInfoEnabled()) {
            logger.info("Submit a scheduled task.");
        }
        scheduledTasks.put(task.getName(), task);
        scheduledConfigs.put(task.getName(), config);
        this.schedule(task);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Submit a scheduled task successfully, name: %s.", task.getName()));
        }
        return this;
    }

    /**
     * 执行一个任务
     *
     * @param task 任务
     */
    private void runTask(Task task) {
        ((BaseTask) task).setStartTime(System.currentTimeMillis());
        ((BaseTask) task).setState(Task.TaskState.RUNNING);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Start invoke the task[%s], state: %s, start time: %s.", task.getName(),
                    task.getState(), DateUtils.get23Date(new Date(task.getStartTime()))));
        }
        try {
            task.invoke();
            ((BaseTask) task).setState(Task.TaskState.FINISHED);
            ((BaseTask) task).setFinishTime(System.currentTimeMillis());
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The task[%s] invoke successfully, state: %s, finish time: %s.",
                        task.getName(), task.getState(), DateUtils.get23Date(new Date(task.getFinishTime()))));
            }
        } catch (Exception ex) {
            ((BaseTask) task).setFinishTime(System.currentTimeMillis());
            ((BaseTask) task).setState(Task.TaskState.ERROR);
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invoke the task[%s] fail, state: %s, finish time: %s.", task.getName(),
                        task.getState(), DateUtils.get23Date(new Date(task.getFinishTime()))), ex);
            }
        }
    }

    /**
     * 调度一个任务
     *
     * @param task 任务
     */
    private void schedule(Task task) {
        Task.ScheduledConfig config = scheduledConfigs.get(task.getName());
        Future<?> future;
        if (config.isOnlyOne()) {
            future = scheduledExecutorService.schedule(() -> runTask(task), config.getDelay(), config.getTimeUnit());
        } else {
            future = scheduledExecutorService.scheduleAtFixedRate(() -> runTask(task), config.getDelay(), config.getPeriod(),
                    config.getTimeUnit());
        }
        if (task instanceof BaseTask) {
            ((BaseTask) task).setFuture(future);
            ((BaseTask) task).setState(Task.TaskState.RUNNING);
            ((BaseTask) task).setStartTime(System.currentTimeMillis());
        }
    }

    private void cancelTask(Task task) {
        if (task instanceof BaseTask && ((BaseTask) task).getFuture() != null &&
                task.getState() == Task.TaskState.RUNNING) {
            ((BaseTask) task).setState(Task.TaskState.ERROR);
            ((BaseTask) task).setFinishTime(System.currentTimeMillis());
            ((BaseTask) task).getFuture().cancel(true);
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The task[%s] will be canceled.", task.getName()));
            }
        }
    }

    /**
     * 关闭当前的任务工厂
     */
    public void shutdown() {
        serialTasks.clear();
        asyncTasks.values().forEach(this::cancelTask);
        asyncTasks.clear();
        scheduledTasks.values().forEach(this::cancelTask);
        scheduledTasks.clear();
        if (singleExecutorService != null) {
            singleExecutorService.shutdownNow();
            singleExecutorService = null;
        }
        if (asyncExecutorPoolService != null) {
            asyncExecutorPoolService.shutdownNow();
            asyncExecutorPoolService = null;
        }
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Shutdown the task factory successfully.");
        }
    }
}
