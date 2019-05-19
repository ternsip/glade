package com.ternsip.glade.universe.common;

import com.ternsip.glade.graphics.renderer.impl.SkyRenderer;
import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@Getter
public class Camera {

    private static final float FOV = 120;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f entityProjectionMatrix;
    private Matrix4f skyProjectionMatrix;

    private float distanceFromRover = 60;
    private float angleAroundRover = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 15;
    private float yaw;
    private float roll;

    private EntityPlayer entityPlayer;

    public Camera(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
        DISPLAY_MANAGER.getDisplayEvents().getScrollCallbacks().add(this::recalculateZoom);
        DISPLAY_MANAGER.getDisplayEvents().getResizeCallbacks().add(this::recalculateProjectionMatrices);
        DISPLAY_MANAGER.getDisplayEvents().getCursorPosCallbacks().add(this::recalculateRotation);
        recalculateProjectionMatrices(DISPLAY_MANAGER.getWidth(), DISPLAY_MANAGER.getHeight());
    }

    private void recalculateRotation(double xPos, double yPos, double dx, double dy) {
        recalculatePitch((float) dy);
        recalculateAngleAroundPlayer((float) dx);
    }

    private void recalculateProjectionMatrices(float width, int height) {
        float ratio = width / height;
        entityProjectionMatrix = createProjectionMatrix(FAR_PLANE, ratio);
        skyProjectionMatrix = createProjectionMatrix(SkyRenderer.SIZE * 2, ratio);
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

    public void move() {
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (entityPlayer.getRotation().y() + angleAroundRover);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = entityPlayer.getRotation().y() + angleAroundRover;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = entityPlayer.getPosition().x - offsetX;
        position.z = entityPlayer.getPosition().z - offsetZ;
        position.y = entityPlayer.getPosition().y + verticalDistance;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromRover * Math.cos(Math.toRadians(pitch)));
    }


    private float calculateVerticalDistance() {
        return (float) (distanceFromRover * Math.sin(Math.toRadians(pitch)));
    }


    private void recalculateZoom(double scrollX, double scrollY) {
        float zoomLevel = (float) (distanceFromRover - scrollY * 5f);
        if (zoomLevel <= 10) {
            distanceFromRover = 10;
        } else if (zoomLevel >= 200) {
            distanceFromRover = 200;
        } else {
            distanceFromRover = zoomLevel;
        }
    }

    private void recalculatePitch(float mouseDy) {
        if (DISPLAY_MANAGER.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
            float pitchChange = pitch + mouseDy * 0.1f;
            if (pitchChange <= -90) {
                pitchChange = -90;
            } else if (pitchChange >= 90) {
                pitchChange = 90;
            } else {
                pitch = pitchChange;
            }
        }
    }

    private void recalculateAngleAroundPlayer(float mouseDx) {
        if (DISPLAY_MANAGER.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
            float angleChange = mouseDx * 0.1f;
            angleAroundRover -= angleChange;
        }
    }

    public Matrix4f createViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(getPitch()), new Vector3f(1, 0, 0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(getYaw()), new Vector3f(0, 1, 0), viewMatrix);
        Vector3f cameraPos = getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        viewMatrix.translate(negativeCameraPos, viewMatrix);
        return viewMatrix;
    }

    public Matrix4f createSkyViewMatrix() {
        Matrix4f matrix = createViewMatrix();
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        return matrix;
    }

    public Matrix4f getProjectionViewMatrix() {
        return entityProjectionMatrix.mul(createViewMatrix(), new Matrix4f());
    }

}