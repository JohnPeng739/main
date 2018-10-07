package org.mx.spring;

import org.mx.spring.task.BaseTask;

public class InitializeTask1 extends BaseTask {
    public InitializeTask1() {
        super("Initialize task1", false);
    }

    public void invoke() {
        System.out.println("Invoke task: ***********.");
    }
}
