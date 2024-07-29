package com.github.javarar.limit.scheduler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LimitSchedulerThreadExecutorTest {
    @Test
    void check() throws InterruptedException {
        LimitSchedulerThreadExecutor l = new LimitSchedulerThreadExecutor(10);
        AtomicInteger i = new AtomicInteger(0);
        l.taskLimit(i::incrementAndGet,
                0,
                500,
                9,
                TimeUnit.MILLISECONDS);
        Thread.sleep(5000);
        Assertions.assertEquals(9, i.get());
    }
}
