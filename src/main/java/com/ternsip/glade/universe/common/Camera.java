package com.ternsip.glade.universe.common;

import com.ternsip.glade.graphics.renderer.impl.SkyRenderer;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
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
    private static final float FOV = 120;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;
    private static final float MIN_DISTANCE_FROM_TARGET = 0.1f;
    private static final float MAX_DISTANCE_FROM_TARGET = 120;
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
        DISPLAY_MANAGER.getDisplayEvents().getScrollCallbacks().add(this::recalculateZoom);
        DISPLAY_MANAGER.getDisplayEvents().getResizeCallbacks().add(this::recalculateProjectionMatrices);
        DISPLAY_MANAGER.getDisplayEvents().getCursorPosCallbacks().add(this::recalculateRotation);
        recalculateProjectionMatrices(DISPLAY_MANAGER.getWidth(), DISPLAY_MANAGER.getHeight());
    }

    public static Matrix4f createProjectionMatrix(float viewDistance, float aspectRatio) {
        //new Matrix4f().perspective(FOV, DISPLAY_MANAGER.getRatio(), NEAR_PLANE, FAR_PLANE).rotate((float) Math.PI, 0, 0, 1);
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float farPlane = viewDistance;
        float frustum_length = farPlane - NEAR_PLANE;

        Matrix4f matrix = new Matrix4f();
        matrix.m00(x_scale);
        matrix.m11(y_scale);
        matrix.m22(-((farPlane + NEAR_PLANE) / frustum_length));
        matrix.m23(-1);
        matrix.m32(-((2 * NEAR_PLANE * farPlane) / frustum_length));
        matrix.m33(0);

        return matrix;
    }

    public static Matrix4fc createOrthoProjectionMatrix(float viewDistance, float width, float height) {
        return new Matrix4f();
    }

    private void recalculateRotation(double xPos, double yPos, double dx, double dy) {
        if (DISPLAY_MANAGER.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
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
        return getDirection().mul(getDistanceFromTarget(), new Vector3f()).add(getTarget().getAdjustedPosition());
    }

    private void recalculateZoom(double scrollX, double scrollY) {
        float newDistance = (float) (getDistanceFromTarget() - scrollY * SCROLL_MULTIPLIER);
        newDistance = Math.min(newDistance, MAX_DISTANCE_FROM_TARGET);
        newDistance = Math.max(newDistance, MIN_DISTANCE_FROM_TARGET);
        setDistanceFromTarget(newDistance);
    }

    public void recalculateViewMatrices() {
        Matrix4fc view = new Matrix4f().lookAt(getPosition(), getTarget().getAdjustedPosition(), UP_DIRECTION);
        setFullViewMatrix(view);
        setSpriteViewMatrix(new Matrix4f().translate(getPosition()));
        setSkyViewMatrix(new Matrix4f(view).m30(0).m31(0).m32(0));
    }

    public Vector3fc getDirection() {
        return FRONT_DIRECTION.rotateX(getRotation().y(), new Vector3f()).rotateY(-getRotation().x());
    }
}