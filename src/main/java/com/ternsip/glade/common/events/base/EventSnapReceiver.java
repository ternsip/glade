package com.ternsip.glade.common.events.base;

import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.common.events.display.MouseButtonEvent;
import com.ternsip.glade.common.logic.Utils;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * That class can potentially be used in any thread to access collected events
 * Essentially, it is receiver to obtain graphical or networking data thread-safely in one batch
 * You can work with fields in the thread without worrying about changes
 * The changes occurs only after calling update method
 * Receiving events is allowed from any thread
 * It is supposed to use callbacks in the original thread only
 * During update method some data may be refreshed in another thread and hence be applied in current
 *
 * @author Ternsip
 */
@Getter
public class EventSnapReceiver {

    private final boolean[] keyPressed = new boolean[512];
    private final boolean[] mouseButtonPressed = new boolean[8];

    private final Map<Class<?>, EventProcessor> eventProcessors = new ConcurrentHashMap<>();
    private final AtomicBoolean applicationActive = new AtomicBoolean(true);

    public EventSnapReceiver() {
        for (Class<? extends Event> clazz : Utils.getAllClasses(Event.class)) {
            eventProcessors.computeIfAbsent(clazz, e -> new EventProcessor());
        }
        registerCallback(KeyEvent.class, (KeyEvent keyEvent) -> {
            keyPressed[keyEvent.getKey()] = keyEvent.getAction() != GLFW_RELEASE;
        });
        registerCallback(MouseButtonEvent.class, (MouseButtonEvent mouseButtonEvent) -> {
            mouseButtonPressed[mouseButtonEvent.getButton()] = mouseButtonEvent.getAction() != GLFW_RELEASE;
        });
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

    public boolean isApplicationActive() {
        return getApplicationActive().get();
    }

    public boolean isKeyDown(int key) {
        return keyPressed[key];
    }

    public boolean isMouseDown(int button) {
        return mouseButtonPressed[button];
    }

}
