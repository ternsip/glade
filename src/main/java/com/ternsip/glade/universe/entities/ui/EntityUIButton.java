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

    private static final long ANIMATION_TIME_MILLISECONDS = 2000;

    private final Callback<CursorPosEvent> cursorPosCallback = this::trackCursor;
    private final Callback<CursorVisibilityEvent> cursorVisibilityCallback = this::handleCursorVisibility;
    private final Callback<MouseButtonEvent> mouseButtonCallback = this::handleMouseButton;
    private final EntitySprite background;
    private final EntitySprite browseOverlay;
    private final EntitySprite pressBackground;

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

    public EntityUIButton(File background, File browseOverlay, File pressBackground, boolean useAspect) {
        super(useAspect);
        this.background = new EntitySprite(background, true, useAspect);
        this.browseOverlay = new EntitySprite(browseOverlay, true, useAspect);
        this.pressBackground = new EntitySprite(pressBackground, true, useAspect);
    }

    @Override
    public void register() {
        super.register();
        getBackground().register();
        getBrowseOverlay().register();
        getPressBackground().register();
        getUniverseClient().getEventIOReceiver().registerCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverseClient().getEventIOReceiver().registerCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverseClient().getEventIOReceiver().registerCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
    }

    @Override
    public void unregister() {
        super.unregister();
        getBackground().unregister();
        getBrowseOverlay().unregister();
        getPressBackground().unregister();
        getUniverseClient().getEventIOReceiver().unregisterCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverseClient().getEventIOReceiver().unregisterCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverseClient().getEventIOReceiver().unregisterCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
    }

    @Override
    public void update() {
        super.update();

        Vector3fc scale = getVisualScale();
        Vector3fc position = getVisualPosition();
        Vector3fc rotation = getVisualRotation();

        getBackground().setScale(scale);
        getBackground().setRotation(rotation);
        getBackground().setPosition(position);
        getBackground().setVisible(isVisible() && !isPressed());

        getBrowseOverlay().setScale(scale);
        getBrowseOverlay().setRotation(rotation);
        getBrowseOverlay().setPosition(new Vector3f(position).add(new Vector3f(0, 0, -0.01f)));
        getBrowseOverlay().setVisible(isVisible() && isCursorInside() && !isPressed());

        getPressBackground().setScale(scale);
        getPressBackground().setRotation(rotation);
        getPressBackground().setPosition(new Vector3f(position).add(new Vector3f(0, 0, -0.02f)));
        getPressBackground().setVisible(isVisible() && isPressed());
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
