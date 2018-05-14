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

    public TaskFactory() {
        super();
        singleExecutorService = Executors.newSingleThreadExecutor();
        asyncExecutorPoolService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 20,
                TimeUnit.SECONDS, new SynchronousQueue<>());
        scheduledExecutorService = Executors.newScheduledThreadPool(10);
    }

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
        singleExecutorService.submit(() -> runTask(task));
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Submit a serial task successfully, name: %s, state: %s, start: %s.",
                    task.getName(), task.getState(), DateUtils.get23Date(new Date(task.getStartTime()))));
        }
        return this;
    }

    public TaskFactory addSingleAsyncTask(Task task) {
        if (logger.isInfoEnabled()) {
            logger.info("Submit a async task.");
        }
        asyncTasks.add(task);
        asyncExecutorPoolService.submit(() -> runTask(task));
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Submit a async task successfully, name: %s, state: %s, start: %s.",
                    task.getName(), task.getState(), DateUtils.get23Date(new Date(task.getStartTime()))));
        }
        return this;
    }

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

    private void runTask(Task task) {
        ((BaseTask) task).setStartTime(System.currentTimeMillis());
        ((BaseTask) task).setState(Task.TaskState.RUNNING);
        task.invoke();
        ((BaseTask) task).setState(Task.TaskState.FINISHED);
        ((BaseTask) task).setFinishTime(System.currentTimeMillis());
    }

    private void schedule(Task task) {
        Task.ScheduledConfig config = scheduledConfigs.get(task.getName());
        // TODO
    }

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
