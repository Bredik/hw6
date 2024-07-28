package com.github.javarar.rejected.task;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class DelayedOnRejectedThreadExecutorTest {
    @Test
    void check() throws InterruptedException, ExecutionException {
        ExecutorService exe = new DelayedOnRejectedThreadExecutor(2, 3).create();

        int taskCount = 10;
        var futures = new ArrayList<Future<Integer>>();

        for (int i = 0; i < taskCount; i++) {
            int finalI = i;
            futures.add(exe.submit(() -> {
                System.out.println("Я выполняюсь " + Thread.currentThread().getName());
                var tmp = finalI;
                return tmp++;
            }));
        }

        exe.shutdown();
        exe.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        Thread.sleep(5000);
        Assertions.assertEquals(10, futures.size());
        for (int i = 0; i < taskCount; i++) {
            Assertions.assertEquals(i, futures.get(i).get());
        }
    }
}
