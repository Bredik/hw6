package com.github.javarar.limit.scheduler;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        LimitSchedulerThreadExecutor l = new LimitSchedulerThreadExecutor(10);

        l.taskLimit(() -> System.out.println("Тик-так"),
                1000,
                1000,
                5,
                TimeUnit.MILLISECONDS);
    }
}
