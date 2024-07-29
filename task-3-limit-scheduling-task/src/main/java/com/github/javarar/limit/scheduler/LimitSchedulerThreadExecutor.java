package com.github.javarar.limit.scheduler;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class LimitSchedulerThreadExecutor extends ScheduledThreadPoolExecutor {
    public LimitSchedulerThreadExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public LimitSchedulerThreadExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public LimitSchedulerThreadExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public LimitSchedulerThreadExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    public void taskLimit(Runnable task, long initialDelay, long period, long limit, TimeUnit unit) {
        if (limit <= 0) {
            log.info("Будем повторять бесконечно");
            super.scheduleAtFixedRate(task, initialDelay, period, unit);
        }

        AtomicLong attempts = new AtomicLong(limit);

        super.scheduleAtFixedRate(() -> {
            if (attempts.get() > 0) {
                task.run();
                attempts.decrementAndGet();
            }
        }, initialDelay, period, unit);
    }
}
