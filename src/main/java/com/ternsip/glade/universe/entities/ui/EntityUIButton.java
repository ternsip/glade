package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.common.events.display.CursorVisibilityEvent;
import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.common.events.display.MouseButtonEvent;
import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.impl.EntityDynamicText2D;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.io.File;
import java.lang.Math;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EntityUIButton extends EntityUI {

    private static final long ANIMATION_TIME_MILLISECONDS = 1000;

    private final Callback<CursorPosEvent> cursorPosCallback = this::trackCursor;
    private final Callback<CursorVisibilityEvent> cursorVisibilityCallback = this::handleCursorVisibility;
    private final Callback<MouseButtonEvent> mouseButtonCallback = this::handleMouseButton;
    private final Callback<KeyEvent> keyEventCallback = this::handleKeyEvent;

    private final EntitySprite picture;
    private final EntityDynamicText2D sign;

    private boolean cursorVisible = false;
    private boolean cursorInside = false;
    private long cursorJoinTime = 0;
    private int pressed = 0;
    private int unpressed = 0;
    private Deque<Integer> keys = new ConcurrentLinkedDeque<>();

    public EntityUIButton(File background, File font, Vector4fc textColor, boolean useAspect, String text) {
        super(useAspect);
        this.picture = new EntitySprite(background, true, useAspect);
        this.picture.register();
        this.sign = new EntityDynamicText2D(font, text, textColor, useAspect);
        this.sign.register();
        registerCallbacks();
    }

    @Override
    public void finish() {
        super.finish();
        getPicture().finish();
        getSign().finish();
        unregisterCallbacks();
    }

    @Override
    public void update(EffigySprite effigy) {
        setRatioX(effigy.getRatioX());
        setRatioY(effigy.getRatioY());

        float textScale = 2f / Math.max(1, getSign().getText().length());
        getSign().setScale(new Vector3f(getVisualScale()).mul(textScale, textScale, 1));
        getSign().setRotation(getVisualRotation());
        getSign().setPosition(new Vector3f(getVisualPosition()).add(0, 0, -0.01f));
        getSign().setVisible(isVisible());

        getPicture().setScale(getVisualScale());
        getPicture().setRotation(getVisualRotation());
        getPicture().setPosition(getVisualPosition());
        getPicture().setVisible(isVisible());
    }

    @Override
    public Vector3fc getVisualScale() {
        if (!isCursorInside()) {
            return getScale();
        }
        float phase = getPhase();
        float scaleCriteria = 0.75f + (float) Math.abs(Math.cos(2 * Math.PI * phase)) * 0.25f;
        return new Vector3f(getScale().x() * scaleCriteria, getScale().y() * scaleCriteria, getScale().z());
    }

    public float getPhase() {
        return ((System.currentTimeMillis() - getCursorJoinTime()) % ANIMATION_TIME_MILLISECONDS) / (float) ANIMATION_TIME_MILLISECONDS;
    }

    public void enable() {
        registerCallbacks();
        setVisible(true);
        resetState();
    }

    public void disable() {
        unregisterCallbacks();
        setVisible(false);
        resetState();
    }

    private void resetState() {
        cursorInside = false;
        pressed = 0;
        unpressed = 0;
    }

    private void registerCallbacks() {
        getUniverse().getEventSnapReceiver().registerCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverse().getEventSnapReceiver().registerCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverse().getEventSnapReceiver().registerCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
        getUniverse().getEventSnapReceiver().registerCallback(KeyEvent.class, getKeyEventCallback());
    }

    private void unregisterCallbacks() {
        getUniverse().getEventSnapReceiver().unregisterCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(KeyEvent.class, getKeyEventCallback());
    }

    private void trackCursor(CursorPosEvent event) {
        boolean cursorInside = isCursorVisible() && isInside((float) event.getNormalX(), (float) event.getNormalY());
        if (!isCursorInside() && cursorInside) {
            setCursorJoinTime(System.currentTimeMillis());
        }
        setCursorInside(cursorInside);
    }

    private void handleKeyEvent(KeyEvent event) {

    }

    private void handleCursorVisibility(CursorVisibilityEvent event) {
        setCursorVisible(event.isVisible());
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

}
