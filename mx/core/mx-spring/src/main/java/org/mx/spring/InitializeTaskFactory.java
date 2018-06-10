package org.mx.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.spring.task.TaskFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 初始化任务工厂
 *
 * @author john peng
 * Date time 2018/6/10 上午11:46
 * @deprecated 在未来版本将被 {@link TaskFactory} 替代。
 */
public class InitializeTaskFactory {
    private static final Log logger = LogFactory.getLog(InitializeTaskFactory.class);
    private static final String TASKS = "initializeTasks";

    private TaskFactory taskFactory;
    private ApplicationContext context;

    /**
     * 默认的构造函数
     *
     * @param context Spring IoC容器上下文
     */
    public InitializeTaskFactory(ApplicationContext context) {
        super();
        this.context = context;
    }

    /**
     * 初始化工厂
     */
    @SuppressWarnings("unchecked")
    public void init() {
        if (context.containsBean(TASKS)) {
            List<Class<InitializeTask>> tasks = (List<Class<InitializeTask>>) context.getBean(TASKS, List.class);
            if (!tasks.isEmpty()) {
                List<InitializeTask> shortTimes = new ArrayList<>();
                List<InitializeTask> longTimes = new ArrayList<>();
                tasks.forEach(taskClass -> {
                    try {
                        final InitializeTask task = taskClass.newInstance();
                        if (task.isLongTimeTask()) {
                            longTimes.add(task);
                        } else {
                            shortTimes.add(task);
                        }
                    } catch (InstantiationException | IllegalAccessException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Initialize the task[%s] fail.", taskClass.getSimpleName()));
                        }
                    }
                });
                taskFactory = new TaskFactory();
                shortTimes.forEach(task -> taskFactory.addSerialTask(task));
                longTimes.forEach(task -> taskFactory.addSingleAsyncTask(task));
            }
        }
    }

    /**
     * 销毁工厂
     */
    public void destroy() {
        if (taskFactory != null) {
            taskFactory.shutdown();
        }
    }
}
