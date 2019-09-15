package com.ternsip.glade.common.logic;

import lombok.Getter;

import java.util.function.Supplier;


@Getter
public class LazyThreadWrapper<T extends Threadable> {

    private final LazyWrapper<ThreadWrapper<T>> wrapper;

    public LazyThreadWrapper(final Supplier<T> supplier) {
        this.wrapper = new LazyWrapper<>(() -> new ThreadWrapper<T>(supplier));
    }

    public LazyThreadWrapper(final Supplier<T> supplier, final long timeout) {
        this.wrapper = new LazyWrapper<>(() -> new ThreadWrapper<T>(supplier, timeout));
    }

    public void touch() {
        getObjective();
    }

    public boolean isInitialized() {
        return getWrapper().isInitialized();
    }

    public ThreadWrapper<T> getThreadWrapper() {
        return getWrapper().getObjective();
    }

    public T getObjective() {
        return getWrapper().getObjective().getObjective();
    }

}
