package com.ternsip.glade.common.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Getter
@Setter
public class ThreadWrapper<T extends Threadable> {

    private final T objective;
    private final Task task;

    public ThreadWrapper(T objective) {
        this.objective = objective;
        this.task = new Task(objective);
        new Thread(task).start();
    }

    public void stop() {
        getTask().setActive(false);
    }

    public boolean isActive() {
        return getTask().isActive();
    }

    @RequiredArgsConstructor
    private static class Task implements Runnable {

        public final AtomicBoolean active = new AtomicBoolean(true);
        public final Threadable threadable;

        public void setActive(boolean active) {
            this.active.set(active);
        }

        public boolean isActive() {
            return this.active.get();
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