package com.ternsip.glade.graphics.camera;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.common.events.display.ScrollEvent;
import com.ternsip.glade.graphics.display.Graphical;
import com.ternsip.glade.graphics.visual.base.Transformable;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@Setter
@Getter
public class ThirdPersonController implements Graphical, CameraController {

    private static final Vector3fc UP_DIRECTION = new Vector3f(0, 1, 0);
    private static final Vector3fc DOWN_DIRECTION = new Vector3f(0, -1, 0);
    private static final Vector3fc BACK_DIRECTION = new Vector3f(0, 0, 1);
    private static final Vector3fc FRONT_DIRECTION = new Vector3f(0, 0, -1);
    private static final Vector3fc LEFT_DIRECTION = new Vector3f(-1, 0, 0);
    private static final Vector3fc RIGHT_DIRECTION = new Vector3f(1, 0, 0);

    private static final float MIN_DISTANCE_FROM_TARGET = 0.1f;
    private static final float MAX_DISTANCE_FROM_TARGET = 320;
    private static final float ROTATION_OVERLAP_EPSILON = 0.001f;
    private static final float MAX_ROTATION_DELTA_X = (float) (Math.PI * 2);
    private static final float MAX_ROTATION_DELTA_Y = (float) (Math.PI / 2 - 0.01f);
    private static final float ROTATION_MULTIPLIER_X = 0.005f;
    private static final float ROTATION_MULTIPLIER_Y = 0.005f;
    private static final float SCROLL_MULTIPLIER = 5f;

    private Vector3f target = new Vector3f(0);
    private float distanceFromTarget = (MAX_DISTANCE_FROM_TARGET + MIN_DISTANCE_FROM_TARGET) * 0.5f;
    private Vector2fc rotation = new Vector2f();

    private Callback<ScrollEvent> scrollCallback = this::recalculateZoom;
    private Callback<CursorPosEvent> cursorPosCallback = this::recalculateRotation;

    public ThirdPersonController() {
        getGraphics().getEventSnapReceiver().registerCallback(ScrollEvent.class, scrollCallback);
        getGraphics().getEventSnapReceiver().registerCallback(CursorPosEvent.class, cursorPosCallback);
    }

    public void update(Transformable transformable) {
        setTarget(transformable.getPosition());
        Camera camera = getGraphics().getGraphicalRepository().getCamera();
        camera.setPosition(getEyePosition());
        camera.setViewMatrix(getViewMatrix());
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
        getGraphics().getEventSnapReceiver().unregisterCallback(ScrollEvent.class, scrollCallback);
        getGraphics().getEventSnapReceiver().unregisterCallback(CursorPosEvent.class, cursorPosCallback);
    }

    private void recalculateRotation(CursorPosEvent event) {
        if (getGraphics().isMouseDown(GLFW_MOUSE_BUTTON_1)) {
            float nx = limitAngle(getRotation().x() + (float) (event.getDx() * ROTATION_MULTIPLIER_X), MAX_ROTATION_DELTA_X);
            float ny = limitAngle(getRotation().y() + (float) (event.getDy() * ROTATION_MULTIPLIER_Y), MAX_ROTATION_DELTA_Y);
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

    private Vector3fc getLookDirection() {
        return BACK_DIRECTION.rotateX(getRotation().y(), new Vector3f()).rotateY(-getRotation().x());
    }

}
