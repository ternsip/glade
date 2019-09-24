package com.ternsip.glade.graphics.camera;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.common.events.display.ScrollEvent;
import com.ternsip.glade.graphics.interfaces.IGraphics;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;

import static com.ternsip.glade.common.logic.Maths.FRONT_DIRECTION;
import static com.ternsip.glade.common.logic.Maths.UP_DIRECTION;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@Setter
@Getter
public class CameraController implements IGraphics, IUniverseClient {

    private static final float MIN_DISTANCE_FROM_TARGET = 0.1f;
    private static final float MAX_DISTANCE_FROM_TARGET = 320;
    private static final float ROTATION_OVERLAP_EPSILON = 0.001f;
    private static final float MAX_ROTATION_DELTA_X = (float) (Math.PI / 2 - 0.01f);
    private static final float MAX_ROTATION_DELTA_Y = (float) (Math.PI * 2);
    private static final float ROTATION_MULTIPLIER_X = 0.005f;
    private static final float ROTATION_MULTIPLIER_Y = -0.005f;
    private static final float SCROLL_MULTIPLIER = 1;
    private static final float FIRST_PERSON_DISTANCE = 1;

    private Vector3fc target = new Vector3f(0);
    private float distanceFromTarget = (MAX_DISTANCE_FROM_TARGET + MIN_DISTANCE_FROM_TARGET) * 0.05f;
    private Vector2fc rotation = new Vector2f();

    private Callback<ScrollEvent> scrollCallback = this::recalculateZoom;
    private Callback<CursorPosEvent> cursorPosCallback = this::recalculateRotation;

    public CameraController() {
        getGraphics().getEventIOReceiverGraphics().registerCallback(ScrollEvent.class, scrollCallback);
        getGraphics().getEventIOReceiverGraphics().registerCallback(CursorPosEvent.class, cursorPosCallback);
    }

    public void update() {
        Camera camera = getGraphics().getCamera();
        camera.setPosition(getEyePosition());
        camera.setViewMatrix(getViewMatrix());

        // TODO make aim take this third person value (instead of pushing it) via update(effigy)
        if (isThirdPerson()) {
            getGraphics().getWindowData().enableCursor();
            getUniverseClient().getEntityClientRepository().getAim().setVisible(false);
        } else {
            getGraphics().getWindowData().disableCursor();
            getUniverseClient().getEntityClientRepository().getAim().setVisible(true);
        }
        getUniverseClient().getSoundRepository().setListenerPosition(getEyePosition());
        getUniverseClient().getSoundRepository().setListenerOrientationFront(getLookDirection());
        getUniverseClient().getSoundRepository().setListenerOrientationUp(getUpDirection());
    }

    public Vector3fc getEyePosition() {
        return getLookDirection()
                .mul(getDistanceFromTarget(), new Vector3f())
                .negate()
                .add(getTarget());
    }

    public Matrix4fc getViewMatrix() {
        // TODO deal with the situation when UP_DIR collinear to camera view
        return new Matrix4f().lookAt(getEyePosition(), getTarget(), UP_DIRECTION);
    }

    public void finish() {
        getGraphics().getEventIOReceiverGraphics().unregisterCallback(ScrollEvent.class, scrollCallback);
        getGraphics().getEventIOReceiverGraphics().unregisterCallback(CursorPosEvent.class, cursorPosCallback);
    }

    public boolean isThirdPerson() {
        return getDistanceFromTarget() > FIRST_PERSON_DISTANCE;
    }

    public Vector3fc getLookDirection() {
        return FRONT_DIRECTION.rotateX(getRotation().x(), new Vector3f()).rotateY(getRotation().y());
    }

    public Vector3fc getUpDirection() {
        return UP_DIRECTION.rotateX(getRotation().x(), new Vector3f()).rotateY(getRotation().y());
    }

    private void recalculateRotation(CursorPosEvent event) {
        if (!isThirdPerson() || getGraphics().getEventIOReceiverGraphics().isMouseDown(GLFW_MOUSE_BUTTON_1)) {
            float nx = limitAngle(getRotation().x() + (float) (event.getDy() * ROTATION_MULTIPLIER_X), MAX_ROTATION_DELTA_X);
            float ny = limitAngle(getRotation().y() + (float) (event.getDx() * ROTATION_MULTIPLIER_Y), MAX_ROTATION_DELTA_Y);
            setRotation(new Vector2f(nx, ny));
        }
    }

    private float limitAngle(float angle, float limit) {
        if (limit > Math.PI * 2 - ROTATION_OVERLAP_EPSILON) {
            return (float) (angle % (Math.PI * 2));
        }
        return Math.max(Math.min(angle, limit), -limit);
    }

    private void recalculateZoom(ScrollEvent event) {
        float newDistance = (float) (getDistanceFromTarget() - event.getYOffset() * SCROLL_MULTIPLIER);
        newDistance = Math.min(newDistance, MAX_DISTANCE_FROM_TARGET);
        newDistance = Math.max(newDistance, MIN_DISTANCE_FROM_TARGET);
        setDistanceFromTarget(newDistance);
    }

}
