package com.ternsip.glade.common.logic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter(value = AccessLevel.PRIVATE)
public class ThreadWrapper<T extends Threadable> {

    private final Task<T> task;
    private final Thread thread;

    public ThreadWrapper(Supplier<T> supplier, long timeout) {
        this.task = new Task<>(supplier, timeout);
        this.thread = new Thread(task);
        this.thread.start();
    }

    public ThreadWrapper(Supplier<T> supplier) {
        this(supplier, 0L);
    }

    public void stop() {
        getTask().setActive(false);
    }

    @SneakyThrows
    public void join() {
        getThread().join();
    }

    public boolean isActive() {
        return getTask().isActive();
    }

    public T getObjective() {
        return getTask().getObjective();
    }

    public void setTimeout(long timeout) {
        getTask().getTimer().setTimeout(timeout);
    }

    @RequiredArgsConstructor
    @Getter
    private static class Task<T extends Threadable> implements Runnable {

        private final AtomicBoolean active = new AtomicBoolean(true);
        private final Supplier<T> supplier;
        private final Timer timer;

        @Getter(lazy = true)
        private final T objective = supplier.get();

        public Task(Supplier<T> supplier, long timeout) {
            this.supplier = supplier;
            this.timer = new Timer(timeout);
        }

        public boolean isActive() {
            return this.active.get();
        }

        public void setActive(boolean active) {
            getActive().set(active);
            if (!getActive().get()) {
                getObjective().unlock();
            }
        }

        @Override
        public void run() {
            getObjective().init();
            while (isActive()) {
                getObjective().update();
                getTimer().rest();
            }
            getObjective().finish();
        }
    }

}
