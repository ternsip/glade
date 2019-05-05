package com.ternsip.glade.entity;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class Camera {

    private float distanceFromRover = 60;
    private float angleAroundRover = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 15;
    private float yaw;
    private float roll;

    private Rover rover;

    public Camera(Rover rover) {
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

    public void move() {
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (rover.getRotY() + angleAroundRover);
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
        float theta = rover.getRotY() + angleAroundRover;
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

}