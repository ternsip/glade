package com.ternsip.glade.common.logic;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Getter
public class LazyWrapper<T> {

    private final Supplier<T> supplier;
    private final AtomicBoolean active = new AtomicBoolean(false);

    @Getter(lazy = true)
    private final T objective = supplier.get();

    public LazyWrapper(Supplier<T> supplier) {
        this.supplier = () -> {
            getActive().set(true);
            return supplier.get();
        };
    }

    public boolean isInitialized() {
        return getActive().get();
    }

}
