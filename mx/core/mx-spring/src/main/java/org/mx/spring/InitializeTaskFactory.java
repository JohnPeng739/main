package org.mx.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("initializeTaskFactory")
public class InitializeTaskFactory implements InitializingBean, DisposableBean {
    private static final Log logger = LogFactory.getLog(InitializeTaskFactory.class);
    private static final String TASKS = "initializeTasks";

    private ExecutorService service = null;

    @Autowired
    private ApplicationContext context = null;

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
                List<Callable<Void>> longTimes = new ArrayList<>();
                tasks.forEach(taskClass -> {
                    try {
                        final InitializeTask task = taskClass.newInstance();
                        if (task.isLongTimeTask()) {
                            longTimes.add(() -> {
                                task.invokeTask();
                                return null;
                            });
                        } else {
                            shortTimes.add(task);
                        }
                    } catch (InstantiationException | IllegalAccessException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Initialize the task[%s] fail.", taskClass.getSimpleName()));
                        }
                    }
                });
                try {
                    service = Executors.newCachedThreadPool();
                    longTimes.add(() -> {
                        for (InitializeTask task : shortTimes) {
                            task.invokeTask();
                        }
                        return null;
                    });
                    service.invokeAll(longTimes);
                } catch (InterruptedException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Invoke task fail.", ex);
                    }
                }
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
        if (service != null) {
            service.shutdownNow();
            service = null;
        }
    }
}
