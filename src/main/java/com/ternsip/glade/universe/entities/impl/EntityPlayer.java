package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.EntityGraphical;
import com.ternsip.glade.universe.graphicals.impl.GraphicalBoy;
import lombok.Getter;
import org.joml.Vector3f;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static org.lwjgl.glfw.GLFW.*;

@Getter
public class EntityPlayer extends EntityGraphical<GraphicalBoy> {

    private static final float RUN_SPEED = 1;
    private static final float TURN_SPEED = 0.02f;
    private static final float GRAVITY = -0.2f;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    @Override
    public GraphicalBoy getVisual() {
        return new GraphicalBoy();
    }

    @Override
    public void update() {
        checkInputs();
        increaseRotation(new Vector3f(0, currentTurnSpeed, 0));
        float distance = currentSpeed;
        float dx = (float) (distance * Math.sin(getRotation().y()));
        float dz = (float) (distance * Math.cos(getRotation().y()));
        increasePosition(new Vector3f(dx, 0, dz));
        upwardsSpeed += GRAVITY;
        increasePosition(new Vector3f(0, upwardsSpeed, 0));
        float terrainHeight = -5;
        if (getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            getPosition().y = terrainHeight;
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
            increaseRotation(new Vector3f(rotX, 0, 0));
        }
        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_N)) {
            increaseRotation(new Vector3f(0, rotY, 0));
        }
        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_M)) {
            increaseRotation(new Vector3f(0, 0, rotZ));
        }

        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_H)) {
            increaseRotation(new Vector3f(-rotX, 0, 0));
        }
        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_J)) {
            increaseRotation(new Vector3f(0, -rotY, 0));
        }
        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_K)) {
            increaseRotation(new Vector3f(0, 0, -rotZ));
        }

        if (DISPLAY_MANAGER.isKeyDown(GLFW_KEY_R)) {
            setRotation(new Vector3f(0, 0, 0));
            setPosition(new Vector3f(600, 30, 550));
        }

    }

}
