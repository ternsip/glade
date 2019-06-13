package com.ternsip.glade.common.events.base;

@FunctionalInterface
public interface Callback<T extends Event> {

    void apply(T event);

}