package com.ternsip.glade.common.events.base;

import com.ternsip.glade.common.logic.Utils;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * That class can potentially be used in any thread to access collected events
 * Essentially, it is receiver to obtain graphical or networking data thread-safely in one batch
 * You can work with fields in the thread without worrying about changes
 * The changes occurs only after calling update method
 * Receiving events is allowed from any thread
 * If you log event with unsafe variables like links to unsafe objects you should:
 * - do that only in the same thread with callbacks
 * - avoid such situation if it is possible
 * It is supposed to use callbacks in the original thread only
 * During update method some data may be refreshed in another thread and hence be applied in current
 *
 * @author Ternsip
 */
@Getter
public class EventReceiver {

    private final ConcurrentHashMap<Class<?>, EventProcessor> eventProcessors = new ConcurrentHashMap<>();

    public EventReceiver() {
        for (Class<? extends Event> clazz : Utils.getAllClasses(Event.class)) {
            eventProcessors.computeIfAbsent(clazz, e -> new EventProcessor());
        }
    }

    public <T extends Event> void registerEvent(Class<T> clazz, T event) {
        getEventProcessor(clazz).registerEvent(event);
    }

    public <T extends Event> void registerCallback(Class<T> clazz, Callback<T> callback) {
        getEventProcessor(clazz).registerCallback(callback);
    }

    public <T extends Event> void unregisterCallback(Class<T> clazz, Callback<T> callback) {
        getEventProcessor(clazz).unregisterCallback(callback);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> EventProcessor<T> getEventProcessor(Class<T> eventClass) {
        return (EventProcessor<T>) getEventProcessors().get(eventClass);
    }

    public void update() {
        getEventProcessors().values().forEach(EventProcessor::applyCallbacks);
        getEventProcessors().values().forEach(EventProcessor::wipeEvents);
    }

}
