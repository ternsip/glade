package com.ternsip.glade.common.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Getter
@Setter
public class ThreadWrapper<T extends Threadable> {

    private final T objective;
    private final Task task;
    private final Thread thread;

    public ThreadWrapper(T objective) {
        this.objective = objective;
        this.task = new Task(objective);
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

    @RequiredArgsConstructor
    private static class Task implements Runnable {

        public final AtomicBoolean active = new AtomicBoolean(true);
        public final Threadable threadable;

        public boolean isActive() {
            return this.active.get();
        }

        public void setActive(boolean active) {
            this.active.set(active);
        }

        @Override
        public void run() {
            this.threadable.init();
            while (isActive()) {
                this.threadable.update();
            }
            this.threadable.finish();
        }
    }

}
