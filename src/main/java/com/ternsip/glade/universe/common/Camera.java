package com.ternsip.glade.universe.common;

import com.ternsip.glade.graphics.display.DisplayManager;
import com.ternsip.glade.graphics.renderer.impl.SkyRenderer;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@Getter
@Setter
public class Camera {

    private static final Vector3fc UP_DIRECTION = new Vector3f(0, 1, 0);
    private static final Vector3fc DOWN_DIRECTION = new Vector3f(0, -1, 0);
    private static final Vector3fc BACK_DIRECTION = new Vector3f(0, 0, 1);
    private static final Vector3fc FRONT_DIRECTION = new Vector3f(0, 0, -1);
    private static final Vector3fc LEFT_DIRECTION = new Vector3f(-1, 0, 0);
    private static final Vector3fc RIGHT_DIRECTION = new Vector3f(1, 0, 0);
    private static final float FOV = (float) Math.toRadians(80);
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;
    private static final float MIN_DISTANCE_FROM_TARGET = 0.1f;
    private static final float MAX_DISTANCE_FROM_TARGET = 320;
    private static final float ROTATION_OVERLAP_EPSILON = 0.001f;
    private static final float MAX_ROTATION_DELTA_X = (float) (Math.PI * 2);
    private static final float MAX_ROTATION_DELTA_Y = (float) (Math.PI / 2 - 0.01f);
    private static final float ROTATION_MULTIPLIER_X = 0.005f;
    private static final float ROTATION_MULTIPLIER_Y = 0.005f;
    private static final float SCROLL_MULTIPLIER = 5f;

    private Matrix4fc entityProjectionMatrix;
    private Matrix4fc skyProjectionMatrix;
    private Matrix4fc spriteProjectionMatrix;

    private float distanceFromTarget = (MAX_DISTANCE_FROM_TARGET + MIN_DISTANCE_FROM_TARGET) * 0.5f;
    private Vector2fc rotation = new Vector2f();
    private Entity target;
    private Matrix4fc fullViewMatrix = new Matrix4f();
    private Matrix4fc spriteViewMatrix = new Matrix4f();
    private Matrix4fc skyViewMatrix = new Matrix4f();

    public Camera(EntityPlayer entityPlayer) {
        this.target = entityPlayer;
        DisplayManager.INSTANCE.getDisplayEvents().getScrollCallbacks().add(this::recalculateZoom);
        DisplayManager.INSTANCE.getDisplayEvents().getResizeCallbacks().add(this::recalculateProjectionMatrices);
        DisplayManager.INSTANCE.getDisplayEvents().getCursorPosCallbacks().add(this::recalculateRotation);
        recalculateProjectionMatrices(DisplayManager.INSTANCE.getWidth(), DisplayManager.INSTANCE.getHeight());
    }

    public static Matrix4f createProjectionMatrix(float viewDistance, float aspectRatio) {
        return new Matrix4f().perspective(FOV, aspectRatio, NEAR_PLANE, viewDistance);
    }

    public static Matrix4fc createOrthoProjectionMatrix(float viewDistance, float width, float height) {
        return new Matrix4f();
    }

    private void recalculateRotation(double xPos, double yPos, double dx, double dy) {
        if (DisplayManager.INSTANCE.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
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

    private void recalculateProjectionMatrices(float width, int height) {
        float ratio = width / height;
        entityProjectionMatrix = createProjectionMatrix(FAR_PLANE, ratio);
        skyProjectionMatrix = createProjectionMatrix(SkyRenderer.SIZE * 2, ratio);
        spriteProjectionMatrix = createOrthoProjectionMatrix(FAR_PLANE, width, height);
    }

    public void update() {
        recalculateViewMatrices();
    }

    public Vector3fc getPosition() {
        return getFrontDirection()
                .mul(getDistanceFromTarget(), new Vector3f())
                .negate()
                .add(getTarget().getAdjustedPosition());
    }

    private void recalculateZoom(double scrollX, double scrollY) {
        float newDistance = (float) (getDistanceFromTarget() - scrollY * SCROLL_MULTIPLIER);
        newDistance = Math.min(newDistance, MAX_DISTANCE_FROM_TARGET);
        newDistance = Math.max(newDistance, MIN_DISTANCE_FROM_TARGET);
        setDistanceFromTarget(newDistance);
    }

    public void recalculateViewMatrices() {
        // TODO deal with the situation when UP_DIR collinear to camera view
        Matrix4fc view = new Matrix4f().lookAt(getPosition(), getTarget().getAdjustedPosition(), UP_DIRECTION);
        setFullViewMatrix(view);
        setSpriteViewMatrix(new Matrix4f().translate(getPosition()));
        setSkyViewMatrix(new Matrix4f(view).m30(0).m31(0).m32(0));
    }

    public Vector3fc getFrontDirection() {
        return BACK_DIRECTION.rotateX(getRotation().y(), new Vector3f()).rotateY(-getRotation().x());
    }
}