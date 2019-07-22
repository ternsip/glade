package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.base.CharEvent;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.common.events.display.CursorVisibilityEvent;
import com.ternsip.glade.common.events.display.KeyEvent;
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
public class EntityUIEditBox extends EntityUI {

    private static final float INNER_FRAME_SCALE_X = 0.9f;
    private static final float INNER_FRAME_SCALE_Y = 0.75f;
    private static final float TEXT_VERTICAL_SCALE = 0.75f;
    private static final long CURSOR_BLINK_TIME_MILLISECONDS = 500L;
    private static final long CURSOR_REST_TIME_MILLISECONDS = 500L;

    private final Callback<CursorPosEvent> cursorPosCallback = this::trackCursor;
    private final Callback<CursorVisibilityEvent> cursorVisibilityCallback = this::handleCursorVisibility;
    private final Callback<MouseButtonEvent> mouseButtonCallback = this::handleMouseButton;
    private final Callback<KeyEvent> keyEventCallback = this::handleKeyEvent;
    private final Callback<CharEvent> charEventCallback = this::handleCharEvent;

    private final EntitySprite background;
    private final EntitySprite frame;
    private final EntitySprite pointer;
    private final EntityDynamicText2D sign;

    private final ArrayList<UICallback> onClick = new ArrayList<>();
    private final ArrayList<UICallback> onPress = new ArrayList<>();
    private final ArrayList<UICallback> onCursorJoin = new ArrayList<>();
    private final ArrayList<UICallback> onCursorLeave = new ArrayList<>();
    private final ArrayList<UICallback> onTextChange = new ArrayList<>();

    private float textCompression = 0.8f;
    private boolean available = false;
    private boolean cursorInside = false;
    private long cursorJoinTime = 0;
    private int pointerPosition = -1;
    private int sliderPosition = 0;
    private int visibleChars = 1;
    private StringBuilder textBuilder = new StringBuilder();
    private long lastPointerActionTime = 0; // TODO use timer class

    public EntityUIEditBox(File background, File frame, File pointer, File font, Vector4fc textColor, boolean useAspect) {
        super(useAspect);

        this.background = new EntitySprite(background, true, useAspect);
        this.background.register();

        this.frame = new EntitySprite(frame, true, useAspect);
        this.frame.register();

        this.pointer = new EntitySprite(pointer, true, useAspect);
        this.pointer.register();

        this.sign = new EntityDynamicText2D(font, "", textColor, useAspect);
        this.sign.register();

        registerCallbacks();
    }

    @Override
    public void finish() {
        super.finish();
        getBackground().finish();
        getFrame().finish();
        getSign().finish();
        unregisterCallbacks();
    }

    @Override
    public void update(EffigySprite effigy) {
        super.update(effigy);

        Vector3fc scale = getVisualScale();
        Vector3fc position = getVisualPosition();
        Vector3fc rotation = getVisualRotation();
        boolean isVisible = isVisible();

        boolean pointerVisible = (System.currentTimeMillis() - getLastPointerActionTime()) < CURSOR_REST_TIME_MILLISECONDS ||
                (System.currentTimeMillis() % (2 * CURSOR_BLINK_TIME_MILLISECONDS)) > CURSOR_BLINK_TIME_MILLISECONDS;

        float textScale = scale.y() * TEXT_VERTICAL_SCALE;
        setVisibleChars(Math.max(1, (int) (2 * scale.x() * INNER_FRAME_SCALE_X / (getTextCompression() * textScale))));
        float signOffsetX = -scale.x() * INNER_FRAME_SCALE_X * getRatioX();
        int pointerBegin = getPointerPosition() - getSliderPosition();
        float pointerOffsetX = signOffsetX + (0.5f + pointerBegin) * getTextCompression() * textScale * getRatioX();
        String text = getTextBuilder().toString();

        getBackground().setScale(scale);
        getBackground().setRotation(rotation);
        getBackground().setPosition(position);
        getBackground().setVisible(isVisible);

        getFrame().setScale(new Vector3f(scale).mul(INNER_FRAME_SCALE_X, INNER_FRAME_SCALE_Y, 1));
        getFrame().setRotation(rotation);
        getFrame().setPosition(new Vector3f(position).add(0, 0, -0.01f));
        getFrame().setVisible(isVisible);

        getSign().setScale(new Vector3f(textScale, textScale, 1));
        getSign().setRotation(rotation);
        getSign().setPosition(new Vector3f(position).add(signOffsetX, 0, -0.02f));
        getSign().setVisible(isVisible);
        getSign().setShiftX(true);
        getSign().setShiftY(false);
        getSign().setTextCompression(getTextCompression());
        getSign().setText(text.substring(getSliderPosition(), Math.min(text.length(), getSliderPosition() + getVisibleChars())));

        getPointer().setScale(new Vector3f(textScale, textScale, 1));
        getPointer().setRotation(rotation);
        getPointer().setPosition(new Vector3f(position).add(pointerOffsetX, 0, -0.03f));
        getPointer().setVisible(pointerVisible && isPointerValid());
    }

    public void insertSymbol(char symbol) {
        getTextBuilder().insert(getPointerPosition(), symbol);
    }

    public void setText(String text) {
        setTextBuilder(new StringBuilder(text));
    }

    public String getText() {
        return getTextBuilder().toString();
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
        invalidatePointer();
    }

    private void registerCallbacks() {
        getUniverse().getEventSnapReceiver().registerCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverse().getEventSnapReceiver().registerCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverse().getEventSnapReceiver().registerCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
        getUniverse().getEventSnapReceiver().registerCallback(KeyEvent.class, getKeyEventCallback());
        getUniverse().getEventSnapReceiver().registerCallback(CharEvent.class, getCharEventCallback());
    }

    private void unregisterCallbacks() {
        getUniverse().getEventSnapReceiver().unregisterCallback(CursorPosEvent.class, getCursorPosCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(MouseButtonEvent.class, getMouseButtonCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(CursorVisibilityEvent.class, getCursorVisibilityCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(KeyEvent.class, getKeyEventCallback());
        getUniverse().getEventSnapReceiver().unregisterCallback(CharEvent.class, getCharEventCallback());
    }

    private void handleKeyEvent(KeyEvent event) {
        if (!isPointerValid() || event.getAction() == GLFW_PRESS) {
            return;
        }
        if (event.getKey() == GLFW_KEY_RIGHT) {
            movePointer(getPointerPosition() + 1);
            return;
        }
        if (event.getKey() == GLFW_KEY_LEFT) {
            movePointer(getPointerPosition() - 1);
            return;
        }
        if (event.getKey() == GLFW_KEY_DELETE && getTextBuilder().length() > getPointerPosition()) {
            getTextBuilder().deleteCharAt(getPointerPosition());
            return;
        }
        if (event.getKey() == GLFW_KEY_BACKSPACE && getTextBuilder().length() > 0) {
            getTextBuilder().deleteCharAt(getPointerPosition() - 1);
            movePointer(getPointerPosition() - 1);
            return;
        }
    }

    private void handleCharEvent(CharEvent charEvent) {
        if (!isPointerValid()) {
            return;
        }
        insertSymbol(Character.toChars(charEvent.getUnicodePoint())[0]);
        movePointer(getPointerPosition() + 1);
    }

    private boolean isPointerValid() {
        return getPointerPosition() >= 0;
    }

    private void invalidatePointer() {
        setPointerPosition(-1);
    }

    private void movePointer(int position) {
        int fixedPosition = Math.max(0, Math.min(getTextBuilder().length(), position));
        setPointerPosition(fixedPosition);
        if (getPointerPosition() < getSliderPosition()) {
            setSliderPosition(getPointerPosition());
        }
        int diff = getPointerPosition() - getSliderPosition() - getVisibleChars() + 1;
        if (diff > 0) {
            setSliderPosition(getSliderPosition() + diff);
        }
        setLastPointerActionTime(System.currentTimeMillis());
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
            resetState();
        }
    }

    private void handleMouseButton(MouseButtonEvent event) {
        if (event.getButton() == GLFW_MOUSE_BUTTON_1) {
            if (isCursorInside()) {
                movePointer(getSliderPosition());
            } else {
                invalidatePointer();
            }
        }
    }

}