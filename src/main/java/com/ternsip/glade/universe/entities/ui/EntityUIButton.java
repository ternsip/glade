package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.common.events.display.CursorVisibilityEvent;
import com.ternsip.glade.common.events.display.MouseButtonEvent;
import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.impl.EntityDynamicText2D;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.io.File;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EntityUIButton extends EntityUI {

    private static final long ANIMATION_SCALE_TIME_MILLISECONDS = 1000;
    private static final long ANIMATION_ROTATE_TIME_MILLISECONDS = 200;

    private final Callback<CursorPosEvent> cursorPosCallback = this::trackCursor;
    private final Callback<CursorVisibilityEvent> cursorVisibilityCallback = this::handleCursorVisibility;
    private final Callback<MouseButtonEvent> mouseButtonCallback = this::handleMouseButton;
    private final EntitySprite picture;
    private final EntityDynamicText2D sign;

    private final ArrayList<ButtonCallback> onClick = new ArrayList<>();
    private final ArrayList<ButtonCallback> onPress = new ArrayList<>();
    private final ArrayList<ButtonCallback> onCursorJoin = new ArrayList<>();
    private final ArrayList<ButtonCallback> onCursorLeave = new ArrayList<>();

    private boolean cursorVisible = false;
    private boolean cursorInside = false;
    private long cursorJoinTime = 0;
    private boolean pressed = false;

    public EntityUIButton(File background, File font, Vector4fc textColor, String text, boolean useAspect) {
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
        float phase = ((System.currentTimeMillis() - getCursorJoinTime()) % ANIMATION_SCALE_TIME_MILLISECONDS) / (float) ANIMATION_SCALE_TIME_MILLISECONDS;
        float scaleCriteria = 0.75f + (float) Math.abs(Math.cos(2 * Math.PI * phase)) * 0.25f;
        return new Vector3f(getScale().x() * scaleCriteria, getScale().y() * scaleCriteria, getScale().z());
    }

    @Override
    public Vector3fc getVisualRotation() {
        if (!isPressed() || !isCursorInside()) {
            return getRotation();
        }
        float phase = ((System.currentTimeMillis() - getCursorJoinTime()) % ANIMATION_ROTATE_TIME_MILLISECONDS) / (float) ANIMATION_ROTATE_TIME_MILLISECONDS;
        float rotateCriteria = (float) (Math.PI * 0.25 * Math.sin(phase * Math.PI));
        return new Vector3f(getRotation().x(), getRotation().y(), getRotation().z() + rotateCriteria);
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
        setCursorInside(false);
        setPressed(false);
    }

    private void registerCallbacks() {
        getUniverse().getEventSnapReceiver().registerCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverse().getEventSnapReceiver().registerCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverse().getEventSnapReceiver().registerCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
    }

    private void unregisterCallbacks() {
        getUniverse().getEventSnapReceiver().unregisterCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
    }

    private void trackCursor(CursorPosEvent event) {
        boolean cursorInside = isCursorVisible() && isInside((float) event.getNormalX(), (float) event.getNormalY());
        if (!isCursorInside() && cursorInside) {
            getOnCursorJoin().forEach(ButtonCallback::execute);
            setCursorJoinTime(System.currentTimeMillis());
        }
        if (isCursorInside() && !cursorInside) {
            getOnCursorLeave().forEach(ButtonCallback::execute);
            setPressed(false);
        }
        setCursorInside(cursorInside);
    }

    private void handleCursorVisibility(CursorVisibilityEvent event) {
        setCursorVisible(event.isVisible());
        if (!event.isVisible()) {
            getOnCursorLeave().forEach(ButtonCallback::execute);
            setCursorInside(false);
            setPressed(false);
        }
    }

    private void handleMouseButton(MouseButtonEvent event) {
        if (event.getButton() == GLFW_MOUSE_BUTTON_1 && isCursorInside()) {
            if (event.getAction() == GLFW_PRESS) {
                setPressed(true);
                getOnPress().forEach(ButtonCallback::execute);
            }
            if (event.getAction() == GLFW_RELEASE && isPressed()) {
                getOnClick().forEach(ButtonCallback::execute);
                setPressed(false);
            }
        }
    }

    @FunctionalInterface
    public interface ButtonCallback {

        void execute();

    }

}
