package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.universe.common.Collision;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static com.ternsip.glade.common.logic.Maths.*;
import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EntityPlayer extends Entity<EffigyBoy> {

    private Vector3f currentVelocity = new Vector3f();
    private Vector3fc lookDirection = new Vector3f(0);
    private Vector3fc moveEffort = new Vector3f(0);
    private float velocity = 5f;

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void update(EffigyBoy effigy) {
        super.update(effigy);
    }

    @Override
    public EffigyBoy getEffigy() {
        return new EffigyBoy();
    }

    @Override
    public void update() {
        checkInputs();
        Vector3f moveDirection = getMoveEffort().rotate(Maths.getRotationQuaternion(getRotation()), new Vector3f());
        getCurrentVelocity().add(getUniverse().getBalance().getGravity());
        Vector3fc nextPosition = new Vector3f(getPosition())
                .add(getCurrentVelocity())
                .add(moveDirection);
        Collision collision = getUniverse().getCollisions().collideSegment(getPosition(), nextPosition);
        setPosition(collision.getPosition());
    }

    private void checkInputs() {

        Vector3f move = new Vector3f(0);
        if (getUniverse().getEventSnapReceiver().isKeyDown(GLFW_KEY_W)) {
            move.add(FRONT_DIRECTION);
        }
        if (getUniverse().getEventSnapReceiver().isKeyDown(GLFW_KEY_S)) {
            move.add(BACK_DIRECTION);
        }
        if (getUniverse().getEventSnapReceiver().isKeyDown(GLFW_KEY_D)) {
            move.add(RIGHT_DIRECTION);
        }
        if (getUniverse().getEventSnapReceiver().isKeyDown(GLFW_KEY_A)) {
            move.add(LEFT_DIRECTION);
        }

        setMoveEffort(normalizeOrEmpty(move).mul(getVelocity(), new Vector3f()));

        if (getUniverse().getEventSnapReceiver().isKeyDown(GLFW_KEY_R)) {
            setRotation(new Vector3f(0, 0, 0));
            setPosition(new Vector3f(600, 30, 550));
        }

    }

}
