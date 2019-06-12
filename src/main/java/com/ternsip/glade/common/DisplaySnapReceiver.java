package com.ternsip.glade.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * That class can potentially be used in any thread to access collected events and window or graphical changes
 * Essentially, it is receiver on logic side to obtain graphical data thread-safely in one batch
 * You can work with fields in the thread without worrying about changes.
 * The changes occurs only after calling update method.
 * You can also put callbacks on events
 * During update method some data may be refreshed in another thread and hence be applied in current
 *
 * @author Ternsip
 */
@Getter
public class DisplaySnapReceiver {

    private final boolean[] keyPressed = new boolean[512];
    private final boolean[] mouseButtonPressed = new boolean[8];

    private final LinkedBlockingQueue<KeyEvent> keyEvents = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<ResizeEvent> resizeEvents = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<MouseButtonEvent> mouseButtonEvents = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<CursorPosEvent> cursorPosEvents = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<ScrollEvent> scrollEvents = new LinkedBlockingQueue<>();

    private final AtomicBoolean applicationActive = new AtomicBoolean(true);

    private final DisplayCallbacks displayCallbacks = new DisplayCallbacks();

    public void update() {
        for (KeyEvent keyEvent : keyEvents) {
            keyPressed[keyEvent.getKey()] = keyEvent.getAction() != GLFW_RELEASE;
        }
        for (MouseButtonEvent mouseButtonEvent : mouseButtonEvents) {
            mouseButtonPressed[mouseButtonEvent.getButton()] = mouseButtonEvent.getAction() != GLFW_RELEASE;
        }
        keyEvents.forEach(e -> getDisplayCallbacks().getKeyCallbacks().forEach(c -> c.apply(e.getKey(), e.getScanCode(), e.getAction(), e.getMods())));
        resizeEvents.forEach(e -> getDisplayCallbacks().getResizeCallbacks().forEach(c -> c.apply(e.getWidth(), e.getHeight())));
        mouseButtonEvents.forEach(e -> getDisplayCallbacks().getMouseButtonCallbacks().forEach(c -> c.apply(e.getButton(), e.getAction(), e.getMods())));
        cursorPosEvents.forEach(e -> getDisplayCallbacks().getCursorPosCallbacks().forEach(c -> c.apply(e.getX(), e.getY(), e.getDx(), e.getDy())));
        scrollEvents.forEach(e -> getDisplayCallbacks().getScrollCallbacks().forEach(c -> c.apply(e.getXOffset(), e.getYOffset())));

        keyEvents.clear();
        resizeEvents.clear();
        mouseButtonEvents.clear();
        cursorPosEvents.clear();
        scrollEvents.clear();
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

    @RequiredArgsConstructor
    @Getter
    public static class ResizeEvent {
        private final int width;
        private final int height;
    }

    @RequiredArgsConstructor
    @Getter
    public static class KeyEvent {
        private final int key;
        private final int scanCode;
        private final int action;
        private final int mods;
    }

    @RequiredArgsConstructor
    @Getter
    public static class MouseButtonEvent {
        private final int button;
        private final int action;
        private final int mods;
    }

    @RequiredArgsConstructor
    @Getter
    public static class CursorPosEvent {
        private final double x;
        private final double y;
        private final double dx;
        private final double dy;
    }

    @RequiredArgsConstructor
    @Getter
    public static class ScrollEvent {
        private final double xOffset;
        private final double yOffset;
    }

}
