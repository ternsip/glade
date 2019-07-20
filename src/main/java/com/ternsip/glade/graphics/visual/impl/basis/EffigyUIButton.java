package com.ternsip.glade.graphics.visual.impl.basis;


import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.common.events.display.CursorVisibilityEvent;
import com.ternsip.glade.common.events.display.MouseButtonEvent;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EffigyUIButton extends EffigySprite {

    private final Callback<CursorPosEvent> cursorPosCallback = this::trackCursor;
    private final Callback<CursorVisibilityEvent> cursorVisibilityCallback = this::handleCursorVisibility;
    private final Callback<MouseButtonEvent> mouseButtonCallback = this::handleMouseButton;
    private Vector2fc uiCenter = new Vector2f(0);
    private Vector2fc uiSize = new Vector2f(1);
    private boolean enabled = true;
    private boolean cursorInside = false;
    private int pressed = 0;
    private int unpressed = 0;

    public EffigyUIButton(File file, boolean useAspect) {
        super(file, true, useAspect);
        registerCallbacks();
    }

    public void enable() {
        registerCallbacks();
        setEnabled(true);
        resetState();
    }

    public void disable() {
        unregisterCallbacks();
        setEnabled(false);
        resetState();
    }

    @Override
    public void finish() {
        super.finish();
        unregisterCallbacks();
    }

    private void resetState() {
        cursorInside = false;
        pressed = 0;
        unpressed = 0;
    }

    private void registerCallbacks() {
        getGraphics().getEventSnapReceiver().registerCallback(CursorPosEvent.class, getCursorPosCallback());
        getGraphics().getEventSnapReceiver().registerCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getGraphics().getEventSnapReceiver().registerCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
    }

    private void unregisterCallbacks() {
        getGraphics().getEventSnapReceiver().unregisterCallback(CursorPosEvent.class, getCursorPosCallback());
        getGraphics().getEventSnapReceiver().unregisterCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getGraphics().getEventSnapReceiver().unregisterCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
    }

    private void trackCursor(CursorPosEvent event) {
        setCursorInside(getGraphics().getWindowData().isCursorEnabled() && isInside((float) event.getNormalX(), (float) event.getNormalY()));
    }

    private void handleCursorVisibility(CursorVisibilityEvent event) {
        if (!event.isVisible()) {
            setCursorInside(false);
        }
    }

    private void handleMouseButton(MouseButtonEvent event) {
        if (event.getButton() == GLFW_MOUSE_BUTTON_1) {
            if (isCursorInside()) {
                pressed += event.getAction() == GLFW_PRESS ? 1 : 0;
            }
            if (pressed > unpressed) {
                unpressed += event.getAction() == GLFW_RELEASE ? 1 : 0;
            }
        }
    }

    private boolean isInside(float x, float y) {
        float sx = getUiCenter().x() - getUiSize().x() * getRatioX();
        float sy = getUiCenter().y() - getUiSize().y() * getRatioY();
        float ex = getUiCenter().x() + getUiSize().x() * getRatioX();
        float ey = getUiCenter().y() + getUiSize().y() * getRatioY();
        return sx <= x && sy <= y && ex >= x && ey >= y;
    }
}
