package com.ternsip.glade.graphics.visual.base.camera;

import com.ternsip.glade.common.DisplayCallbacks;
import com.ternsip.glade.graphics.display.Displayable;
import com.ternsip.glade.graphics.visual.base.graphical.Transformable;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@Setter
@Getter
public class ThirdPersonController implements Displayable, CameraController {

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

    private DisplayCallbacks.ScrollCallback scrollCallback = this::recalculateZoom;
    private DisplayCallbacks.CursorPosCallback cursorPosCallback = this::recalculateRotation;

    public ThirdPersonController() {
        getDisplayManager().getDisplayCallbacks().getScrollCallbacks().add(scrollCallback);
        getDisplayManager().getDisplayCallbacks().getCursorPosCallbacks().add(cursorPosCallback);
    }

    public void update(Transformable transformable) {
        setTarget(transformable.getPosition());
        Camera camera = getDisplayManager().getGraphicalRepository().getCamera();
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
        getDisplayManager().getDisplayCallbacks().getScrollCallbacks().remove(getScrollCallback());
        getDisplayManager().getDisplayCallbacks().getCursorPosCallbacks().remove(getCursorPosCallback());
    }

    private void recalculateRotation(double xPos, double yPos, double dx, double dy) {
        if (getDisplayManager().isMouseDown(GLFW_MOUSE_BUTTON_1)) {
            float nx = limitAngle(getRotation().x() + (float) (dx * ROTATION_MULTIPLIER_X), MAX_ROTATION_DELTA_X);
            float ny = limitAngle(getRotation().y() + (float) (dy * ROTATION_MULTIPLIER_Y), MAX_ROTATION_DELTA_Y);
            setRotation(new Vector2f(nx, ny));
        }
    }

    private float limitAngle(float angle, float limit) {
        if (limit > Math.PI * 2 - ROTATION_OVERLAP_EPSILON) {
            return (float) (angle % (Math.PI * 2));
        }
        return Math.max(Math.min(angle, limit), -limit);
    }

    private void recalculateZoom(double scrollX, double scrollY) {
        float newDistance = (float) (getDistanceFromTarget() - scrollY * SCROLL_MULTIPLIER);
        newDistance = Math.min(newDistance, MAX_DISTANCE_FROM_TARGET);
        newDistance = Math.max(newDistance, MIN_DISTANCE_FROM_TARGET);
        setDistanceFromTarget(newDistance);
    }

    private Vector3fc getLookDirection() {
        return BACK_DIRECTION.rotateX(getRotation().y(), new Vector3f()).rotateY(-getRotation().x());
    }

}
