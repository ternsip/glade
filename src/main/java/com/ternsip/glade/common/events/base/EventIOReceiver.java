package com.ternsip.glade.common.events.base;

import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.common.events.display.MouseButtonEvent;
import lombok.Getter;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Getter
public class EventIOReceiver extends EventReceiver {

    private final boolean[] keyPressed = new boolean[512];
    private final boolean[] mouseButtonPressed = new boolean[8];

    public EventIOReceiver() {
        super();
        registerCallback(KeyEvent.class, (KeyEvent keyEvent) -> {
            keyPressed[keyEvent.getKey()] = keyEvent.getAction() != GLFW_RELEASE;
        });
        registerCallback(MouseButtonEvent.class, (MouseButtonEvent mouseButtonEvent) -> {
            mouseButtonPressed[mouseButtonEvent.getButton()] = mouseButtonEvent.getAction() != GLFW_RELEASE;
        });
    }

    public boolean isKeyDown(int key) {
        return keyPressed[key];
    }

    public boolean isMouseDown(int button) {
        return mouseButtonPressed[button];
    }

}
