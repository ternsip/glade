package com.ternsip.glade.common.logic;

public interface Threadable {

    void init();

    void update();

    void finish();

    default void lock() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

    default void unlock() {
        synchronized (this) {
            this.notify();
        }
    }

}
