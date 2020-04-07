package com.ternsip.glade.common.logic;

import lombok.SneakyThrows;

public interface Threadable {

    void init();

    void update();

    void finish();

    @SneakyThrows
    default void lock() {
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
