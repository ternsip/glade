package com.ternsip.glade.common.events.base;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.concurrent.LinkedBlockingQueue;

@Getter(AccessLevel.PRIVATE)
public class EventProcessor<T extends Event> {

    private final LinkedBlockingQueue<Callback<T>> callbacks = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<T> events = new LinkedBlockingQueue<>();

    public void registerCallback(Callback<T> callback) {
        getCallbacks().add(callback);
    }

    public void unregisterCallback(Callback<T> callback) {
        getCallbacks().remove(callback);
    }

    public void registerEvent(T event) {
        getEvents().add(event);
    }

    void update() {
        while (!getEvents().isEmpty()) {
            T event = events.poll();
            getCallbacks().forEach(callback -> callback.apply(event));
        }
    }

}