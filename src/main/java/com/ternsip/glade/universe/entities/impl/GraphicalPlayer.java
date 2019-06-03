package com.ternsip.glade.universe.entities.impl;

import org.joml.Vector3f;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static org.lwjgl.glfw.GLFW.*;

public class GraphicalPlayer extends GraphicalBoy {

    private static final float RUN_SPEED = 90;
    private static final float TURN_SPEED = 2.0f;
    private static final float GRAVITY = -20;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    public GraphicalPlayer() {
        increaseRotation(new Vector3f(0, 0, (float) (-Math.PI / 2.0f)));
    }

    @Override
    public void update() {
        checkInputs();
        super.increaseRotation(new Vector3f(0, currentTurnSpeed * DISPLAY_MANAGER.getDeltaTime(), 0));
        float distance = currentSpeed * DISPLAY_MANAGER.getDeltaTime();
        float dx = (float) (distance * Math.sin(super.getRotation().y()));
        float dz = (float) (distance * Math.cos(super.getRotation().y()));
        super.increasePosition(new Vector3f(dx, 0, dz));
        upwardsSpeed += GRAVITY * DISPLAY_MANAGER.getDeltaTime();
        super.increasePosition(new Vector3f(0, upwardsSpeed * DISPLAY_MANAGER.getDeltaTime(), 0));
        float terrainHeight = -5;
        if (super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            super.getPosition().y = terrainHeight;
        }
    }

    private void checkInputs() {

        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_W)) {
            this.currentSpeed = +RUN_SPEED;
        } else if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        float rotX = 0.60f;
        float rotY = 0.60f;
        float rotZ = 0.60f;

        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_B)) {
            super.increaseRotation(new Vector3f(rotX, 0, 0));
        }
        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_N)) {
            super.increaseRotation(new Vector3f(0, rotY, 0));
        }
        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_M)) {
            super.increaseRotation(new Vector3f(0, 0, rotZ));
        }

        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_H)) {
            super.increaseRotation(new Vector3f(-rotX, 0, 0));
        }
        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_J)) {
            super.increaseRotation(new Vector3f(0, -rotY, 0));
        }
        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_K)) {
            super.increaseRotation(new Vector3f(0, 0, -rotZ));
        }

        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_R)) {
            setRotation(new Vector3f(0, 0, 0));
            this.setPosition(new Vector3f(600, 30, 550));
        }

    }

}
