package org.mx.spring;

public class InitializeTask1 extends InitializeTask {
    public InitializeTask1() {
        super("Initialize task1", false);
    }

    public void invokeTask() {
        System.out.println("Invoke task: ***********.");
    }
}
