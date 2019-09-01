package com.ternsip.glade.common.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class LazyWrapper<T> {

    private final Supplier<T> supplier;

    @Getter(lazy = true)
    private final T objective = supplier.get();

}
