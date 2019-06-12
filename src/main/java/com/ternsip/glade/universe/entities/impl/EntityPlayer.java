package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.EffigyBoy;
import com.ternsip.glade.universe.entities.base.EntityTransformable;
import lombok.Getter;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

@Getter
public class EntityPlayer extends EntityTransformable<EffigyBoy> {

    private static final float RUN_SPEED = 5;
    private static final float TURN_SPEED = 0.02f;
    private static final float GRAVITY = -0.2f;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    @Override
    public void update(EffigyBoy visual) {
        super.update(visual);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public EffigyBoy getVisual() {
        return new EffigyBoy();
    }

    @Override
    public void update() {
        checkInputs();
        increaseRotation(new Vector3f(0, currentTurnSpeed, 0));
        float dx = (float) (currentSpeed * Math.sin(getRotation().y()));
        float dz = (float) (currentSpeed * Math.cos(getRotation().y()));
        upwardsSpeed += GRAVITY;
        increasePosition(new Vector3f(dx, upwardsSpeed, dz));
        float terrainHeight = -5;
        if (getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            getPosition().y = terrainHeight;
        }
    }

    private void checkInputs() {

        if (getUniverse().getDisplaySnapReceiver().isKeyDown(GLFW_KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        } else if (getUniverse().getDisplaySnapReceiver().isKeyDown(GLFW_KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (getUniverse().getDisplaySnapReceiver().isKeyDown(GLFW_KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (getUniverse().getDisplaySnapReceiver().isKeyDown(GLFW_KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if (getUniverse().getDisplaySnapReceiver().isKeyDown(GLFW_KEY_R)) {
            setRotation(new Vector3f(0, 0, 0));
            setPosition(new Vector3f(600, 30, 550));
        }

    }

}
