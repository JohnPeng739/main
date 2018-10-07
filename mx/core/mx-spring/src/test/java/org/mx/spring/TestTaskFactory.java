package org.mx.spring;

import org.mx.spring.task.BaseTask;
import org.mx.spring.task.TaskFactory;

/**
 * 描述：
 *
 * @author : john date : 2018/9/4 下午9:14
 */
public class TestTaskFactory {
    public static void main(String[] args) {
        TaskFactory taskFactory = new TaskFactory();
        for (int index = 1; index < 10; index ++) {
            BaseTask task = new TestTask(index);
            System.out.println(String.format("task name: %s, task state: %s.", task.getName(), task.getState()));
            taskFactory.addSingleAsyncTask(task);
            System.out.println(String.format("task name: %s, task state: %s.", task.getName(), task.getState()));
        }
    }

    static public class TestTask extends BaseTask {
        public TestTask(int index) {
            super("test task " + index, true);
        }

        @Override
        public void invoke() {
            System.out.println("enter the task " + super.getName() + " ...");
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("exit the task " + super.getName() + ".");
        }
    }
}
