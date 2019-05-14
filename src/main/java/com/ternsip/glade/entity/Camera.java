package com.ternsip.glade.entity;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@Getter
public class Camera {

    private static final float FOV = 100;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    private float distanceFromRover = 60;
    private float angleAroundRover = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 15;
    private float yaw;
    private float roll;

    private Rover rover;

    public Camera(Rover rover) {
        this.projectionMatrix = createProjectionMatrix();
        this.rover = rover;
        DISPLAY_MANAGER.registerScrollCallback(((window, xoffset, yoffset) -> {
            recalculateZoom((float) yoffset);
        }));

        DISPLAY_MANAGER.registerCursorPosCallback((new GLFWCursorPosCallbackI() {

            private float dx;
            private float dy;
            private float prevX;
            private float prevY;

            @Override
            public void invoke(long window, double xpos, double ypos) {
                dx = (float) (xpos - prevX);
                dy = (float) (ypos - prevY);
                prevX = (float) xpos;
                prevY = (float) ypos;
                Camera.this.recalculatePitch(dy);
                Camera.this.recalculateAngleAroundRover(dx);
            }
        }));

    }

    public static Matrix4f createProjectionMatrix() {
        float aspectRatio = (float) DISPLAY_MANAGER.getWidth() / (float) DISPLAY_MANAGER.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        Matrix4f matrix = new Matrix4f();
        matrix.m00(x_scale);
        matrix.m11(y_scale);
        matrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        matrix.m23(-1);
        matrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        matrix.m33(0);

        return matrix;
    }

    public void move() {
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (rover.getRotation().y() + angleAroundRover);
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
        float theta = rover.getRotation().y() + angleAroundRover;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = rover.getPosition().x - offsetX;
        position.z = rover.getPosition().z - offsetZ;
        position.y = rover.getPosition().y + verticalDistance;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromRover * Math.cos(Math.toRadians(pitch)));
    }


    private float calculateVerticalDistance() {
        return (float) (distanceFromRover * Math.sin(Math.toRadians(pitch)));
    }


    private void recalculateZoom(float mouseWheelVelocity) {
        float zoomLevel = distanceFromRover - mouseWheelVelocity * 5f;
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

    private void recalculateAngleAroundRover(float mouseDx) {
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
        return projectionMatrix.mul(createViewMatrix(), new Matrix4f());
    }

}