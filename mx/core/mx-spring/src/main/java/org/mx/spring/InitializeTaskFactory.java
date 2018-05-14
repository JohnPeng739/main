package org.mx.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.spring.task.TaskFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("initializeTaskFactory")
public class InitializeTaskFactory implements InitializingBean, DisposableBean {
    private static final Log logger = LogFactory.getLog(InitializeTaskFactory.class);
    private static final String TASKS = "initializeTasks";

    private TaskFactory taskFactory;
    private ApplicationContext context;

    /**
     * 默认的构造函数
     *
     * @param context Spring IoC容器上下文
     */
    @Autowired
    public InitializeTaskFactory(ApplicationContext context) {
        super();
        this.context = context;
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        if (context.containsBean(TASKS)) {
            List<Class<InitializeTask>> tasks = (List<Class<InitializeTask>>) context.getBean(TASKS, List.class);
            if (tasks != null && !tasks.isEmpty()) {
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
     * {@inheritDoc}
     *
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (taskFactory != null) {
            taskFactory.shutdown();
        }
    }
}
