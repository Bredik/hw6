package com.github.javarar.rejected.task;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.*;

@Log4j2
public class DelayedOnRejectedThreadExecutor {
    private final ScheduledExecutorService scheduledExecutor;
    private static final int rejectRetryDelay = 2;
    private static final TimeUnit rejectRetryTimeUnit = TimeUnit.SECONDS;

    private final int poolSize;
    private final int queueSize;

    public DelayedOnRejectedThreadExecutor(int poolSize, int queueSize) {
        this.poolSize = poolSize;
        this.queueSize = queueSize;
        this.scheduledExecutor = Executors.newScheduledThreadPool(30);
    }

    public ExecutorService create() {
        BlockingQueue<Runnable> queueMain = new ArrayBlockingQueue<>(queueSize);
        return new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0,
                TimeUnit.SECONDS,
                queueMain,
                new RepeaterPolicy());
    }

    public class RepeaterPolicy implements RejectedExecutionHandler {
        public RepeaterPolicy() {
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            rejectedHandler(r, executor);
        }
    }

    private void rejectedHandler(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
        log.error("Задача не поместилась: {}", Thread.currentThread().getName());
        scheduledExecutor.schedule(() -> {
                    System.out.println("Пробуем добавить задачу заново " + Thread.currentThread().getName());
                    Executors.newSingleThreadExecutor().submit(runnable);
                },
                rejectRetryDelay,
                rejectRetryTimeUnit);
    }

}
