package org.mx.spring.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DateUtils;

import java.util.*;
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

    private List<Task> serialTasks = new ArrayList<>();
    private List<Task> asyncTasks = new ArrayList<>();
    private List<Task> scheduledTasks = new ArrayList<>();
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
    public List<Task> getSerialTasks() {
        return serialTasks;
    }

    /**
     * 获取所有的单次异步任务
     *
     * @return 单次异步任务列表
     */
    public List<Task> getAsyncTasks() {
        return asyncTasks;
    }

    /**
     * 获取所有的可调度任务
     *
     * @return 可调度任务列表
     */
    public List<Task> getScheduledTasks() {
        return scheduledTasks;
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
        serialTasks.add(task);
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
            singleExecutorService.submit(() -> serialTasks.forEach(this::runTask));
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
        asyncTasks.add(task);
        try {
            asyncExecutorPoolService.submit(() -> runTask(task));
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Submit a async task successfully, name: %s, state: %s, start: %s.",
                        task.getName(), task.getState(), DateUtils.get23Date(new Date(task.getStartTime()))));
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
        scheduledTasks.add(task);
        scheduledConfigs.put(task.getName(), config);
        this.schedule(task);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Submit a scheduled task successfully, name: %s, state: %s, start: %s.",
                    task.getName(), task.getState(), DateUtils.get23Date(new Date(task.getStartTime()))));
        }
        return this;
    }

    /**
     * 执行一个任务
     *
     * @param task 任务
     */
    private void runTask(Task task) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Start invoke the task[%s]......", task.getName()));
        }
        ((BaseTask) task).setStartTime(System.currentTimeMillis());
        ((BaseTask) task).setState(Task.TaskState.RUNNING);
        try {
            task.invoke();
            ((BaseTask) task).setState(Task.TaskState.FINISHED);
            ((BaseTask) task).setFinishTime(System.currentTimeMillis());
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The task[%s] invoke successfully.", task.getName()));
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invoke the task[%s] fail.", task.getName()), ex);
            }
            ((BaseTask) task).setFinishTime(System.currentTimeMillis());
            ((BaseTask) task).setState(Task.TaskState.ERROR);
        }
    }

    /**
     * 调度一个任务
     *
     * @param task 任务
     */
    private void schedule(Task task) {
        Task.ScheduledConfig config = scheduledConfigs.get(task.getName());
        if (config.isOnlyOne()) {
            scheduledExecutorService.schedule(() -> runTask(task), config.getDelay(), config.getTimeUnit());
        } else {
            scheduledExecutorService.scheduleAtFixedRate(() -> runTask(task), config.getDelay(), config.getPeriod(),
                    config.getTimeUnit());
        }
    }

    /**
     * 关闭当前的任务工厂
     */
    public void shutdown() {
        serialTasks.clear();
        asyncTasks.clear();
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
