package com.ternsip.glade.graphics.display;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
public class DisplaySnapReceiver implements Displayable  {

    private final boolean[] keyPressed = new boolean[512];
    private final boolean[] mouseButtonPressed = new boolean[8];

    private final List<DisplaySnapCollector.KeyEvent> keyEvents = new ArrayList<>();
    private final List<DisplaySnapCollector.ResizeEvent> resizeEvents = new ArrayList<>();
    private final List<DisplaySnapCollector.MouseButtonEvent> mouseButtonEvents = new ArrayList<>();
    private final List<DisplaySnapCollector.CursorPosEvent> cursorPosEvents = new ArrayList<>();
    private final List<DisplaySnapCollector.ScrollEvent> scrollEvents = new ArrayList<>();

    private final DisplayCallbacks displayCallbacks = new DisplayCallbacks();

    private boolean applicationActive = true;

    public void update() {
        DisplaySnapCollector collector = getDisplayManager().getDisplaySnapCollector();

        if (!keyEvents.isEmpty() || !collector.getKeyEvents().isEmpty()) {
            keyEvents.clear();
            collector.getKeyEvents().drainTo(keyEvents);
        }

        if (!resizeEvents.isEmpty() || !collector.getResizeEvents().isEmpty()) {
            resizeEvents.clear();
            collector.getResizeEvents().drainTo(resizeEvents);
        }

        if (!mouseButtonEvents.isEmpty() || !collector.getMouseButtonEvents().isEmpty()) {
            mouseButtonEvents.clear();
            collector.getMouseButtonEvents().drainTo(mouseButtonEvents);
        }

        if (!cursorPosEvents.isEmpty() || !collector.getCursorPosEvents().isEmpty()) {
            cursorPosEvents.clear();
            collector.getCursorPosEvents().drainTo(cursorPosEvents);
        }

        if (!scrollEvents.isEmpty() || !collector.getScrollEvents().isEmpty()) {
            scrollEvents.clear();
            collector.getScrollEvents().drainTo(scrollEvents);
        }

        for (DisplaySnapCollector.KeyEvent keyEvent : keyEvents) {
            keyPressed[keyEvent.getKey()] = keyEvent.getAction() != GLFW_RELEASE;
        }
        for (DisplaySnapCollector.MouseButtonEvent mouseButtonEvent : mouseButtonEvents) {
            mouseButtonPressed[mouseButtonEvent.getButton()] = mouseButtonEvent.getAction() != GLFW_RELEASE;
        }
        keyEvents.forEach(e -> getDisplayCallbacks().getKeyCallbacks().forEach(c -> c.apply(e.getKey(), e.getScanCode(), e.getAction(), e.getMods())));
        resizeEvents.forEach(e -> getDisplayCallbacks().getResizeCallbacks().forEach(c -> c.apply(e.getWidth(), e.getHeight())));
        mouseButtonEvents.forEach(e -> getDisplayCallbacks().getMouseButtonCallbacks().forEach(c -> c.apply(e.getButton(), e.getAction(), e.getMods())));
        cursorPosEvents.forEach(e -> getDisplayCallbacks().getCursorPosCallbacks().forEach(c -> c.apply(e.getX(), e.getY(), e.getDx(), e.getDy())));
        scrollEvents.forEach(e -> getDisplayCallbacks().getScrollCallbacks().forEach(c -> c.apply(e.getXOffset(), e.getYOffset())));

        applicationActive = collector.getApplicationActive().get();
    }

    public boolean isKeyDown(int key) {
        return keyPressed[key];
    }

    public boolean isMouseDown(int button) {
        return mouseButtonPressed[button];
    }

}
