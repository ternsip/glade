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

    public ThreadWrapper(Supplier<T> supplier) {
        this.task = new Task<>(supplier);
        this.thread = new Thread(task);
        this.thread.start();
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

    @RequiredArgsConstructor
    private static class Task<T extends Threadable> implements Runnable {

        private final AtomicBoolean active = new AtomicBoolean(true);
        private final Supplier<T> supplier;

        @Getter(lazy = true)
        private final T objective = supplier.get();

        public boolean isActive() {
            return this.active.get();
        }

        public void setActive(boolean active) {
            this.active.set(active);
            if (!this.active.get()) {
                getObjective().unlock();
            }
        }

        @Override
        public void run() {
            getObjective().init();
            while (isActive()) {
                getObjective().update();
            }
            getObjective().finish();
        }
    }

}
