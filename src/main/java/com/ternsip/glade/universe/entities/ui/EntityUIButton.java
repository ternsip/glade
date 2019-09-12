package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.common.events.display.CursorVisibilityEvent;
import com.ternsip.glade.common.events.display.MouseButtonEvent;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.File;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EntityUIButton extends EntityUI {

    private static final long ANIMATION_TIME_MILLISECONDS = 1000;

    private final Callback<CursorPosEvent> cursorPosCallback = this::trackCursor;
    private final Callback<CursorVisibilityEvent> cursorVisibilityCallback = this::handleCursorVisibility;
    private final Callback<MouseButtonEvent> mouseButtonCallback = this::handleMouseButton;
    private final EntitySprite background;
    private final EntitySprite browseOverlay;
    private final EntitySprite pressOverlay;

    private final ArrayList<UICallback> onClick = new ArrayList<>();
    private final ArrayList<UICallback> onPress = new ArrayList<>();
    private final ArrayList<UICallback> onRelease = new ArrayList<>();
    private final ArrayList<UICallback> onCursorJoin = new ArrayList<>();
    private final ArrayList<UICallback> onCursorLeave = new ArrayList<>();

    private boolean available = true;
    private boolean cursorInside = false;
    private long cursorJoinTime = 0;
    private boolean pressed = false;
    private boolean animated = true;

    public EntityUIButton(File background, File browseOverlay, File pressOverlay, boolean useAspect) {
        super(useAspect);
        this.background = new EntitySprite(background, true, useAspect);
        this.browseOverlay = new EntitySprite(browseOverlay, true, useAspect);
        this.pressOverlay = new EntitySprite(pressOverlay, true, useAspect);
    }

    @Override
    public void register() {
        super.register();
        getBackground().register();
        getBrowseOverlay().register();
        getPressOverlay().register();
        getUniverseClient().getEventSnapReceiver().registerCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverseClient().getEventSnapReceiver().registerCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverseClient().getEventSnapReceiver().registerCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
    }

    @Override
    public void unregister() {
        super.unregister();
        getBackground().unregister();
        getBrowseOverlay().unregister();
        getPressOverlay().unregister();
        getUniverseClient().getEventSnapReceiver().unregisterCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverseClient().getEventSnapReceiver().unregisterCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverseClient().getEventSnapReceiver().unregisterCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
    }

    @Override
    public void clientUpdate() {
        super.clientUpdate();

        Vector3fc scale = getVisualScale();
        Vector3fc position = getVisualPosition();
        Vector3fc rotation = getVisualRotation();

        getBackground().setScale(scale);
        getBackground().setRotation(rotation);
        getBackground().setPosition(position);
        getBackground().setVisible(isVisible());

        getBrowseOverlay().setScale(scale);
        getBrowseOverlay().setRotation(rotation);
        getBrowseOverlay().setPosition(new Vector3f(position).add(new Vector3f(0, 0, -0.01f)));
        getBrowseOverlay().setVisible(isVisible() && isCursorInside());

        getPressOverlay().setScale(scale);
        getPressOverlay().setRotation(rotation);
        getPressOverlay().setPosition(new Vector3f(position).add(new Vector3f(0, 0, -0.02f)));
        getPressOverlay().setVisible(isVisible() && isPressed());
    }

    public Vector3fc getVisualScale() {
        if (!isCursorInside() || !isAnimated()) {
            return getScale();
        }
        float phase = ((System.currentTimeMillis() - getCursorJoinTime()) % ANIMATION_TIME_MILLISECONDS) / (float) ANIMATION_TIME_MILLISECONDS;
        float scaleCriteria = 0.8f + (float) Math.abs(Math.cos(2 * Math.PI * phase)) * 0.20f;
        return new Vector3f(getScale().x() * scaleCriteria, getScale().y() * scaleCriteria, getScale().z());
    }

    private void trackCursor(CursorPosEvent event) {
        boolean cursorInside = isAvailable() && isInside((float) event.getNormalX(), (float) event.getNormalY());
        if (!isCursorInside() && cursorInside) {
            getOnCursorJoin().forEach(UICallback::execute);
            setCursorJoinTime(System.currentTimeMillis());
        }
        if (isCursorInside() && !cursorInside) {
            getOnCursorLeave().forEach(UICallback::execute);
        }
        setCursorInside(cursorInside);
    }

    private void handleCursorVisibility(CursorVisibilityEvent event) {
        setAvailable(event.isVisible());
        if (!event.isVisible()) {
            getOnCursorLeave().forEach(UICallback::execute);
            setCursorInside(false);
            if (isPressed()) {
                getOnRelease().forEach(UICallback::execute);
            }
            setPressed(false);
        }
    }

    private void handleMouseButton(MouseButtonEvent event) {
        if (event.getButton() == GLFW_MOUSE_BUTTON_1) {
            if (event.getAction() == GLFW_PRESS && isCursorInside()) {
                setPressed(true);
                getOnPress().forEach(UICallback::execute);
            }
            if (event.getAction() == GLFW_RELEASE && isPressed()) {
                getOnRelease().forEach(UICallback::execute);
                setPressed(false);
                if (isCursorInside()) {
                    getOnClick().forEach(UICallback::execute);
                }
            }
        }
    }

}
