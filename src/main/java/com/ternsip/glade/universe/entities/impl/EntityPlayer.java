package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.universe.collisions.base.Collision;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import java.util.List;

import static com.ternsip.glade.common.logic.Maths.*;
import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EntityPlayer extends Entity<EffigyBoy> {

    private Vector3f currentVelocity = new Vector3f();
    private Vector3fc lookDirection = new Vector3f(0);
    private Vector3fc moveEffort = new Vector3f(0);
    private float velocity = 0.1f;
    private float jumpPower = 0.3f;
    private boolean onTheGround = false;

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
        Vector3fc cPos = getPosition();
        Vector3fc nPos = new Vector3f(cPos)
                .add(getCurrentVelocity())
                .add(moveDirection);
        Vector3fc tryX = tryToMove(cPos, new Vector3f(nPos.x(), cPos.y(), cPos.z()));
        Vector3fc tryY = tryToMove(tryX, new Vector3f(tryX.x(), nPos.y(), cPos.z()));
        Vector3fc tryZ = tryToMove(tryY, new Vector3f(tryY.x(), tryY.y(), nPos.z()));
        setOnTheGround(tryToMove(cPos, new Vector3f(cPos).add(DOWN_DIRECTION)).equals(cPos, 5 * EPS));
        if (isOnTheGround()) {
            getCurrentVelocity().y = 0;
        }
        setPosition(tryZ);
    }

    public Vector3i getBlockPositionStandingOn() {
        return new Vector3i(
                (int) Math.floor(getPosition().x()),
                (int) Math.floor(getPosition().y()) - 1,
                (int) Math.floor(getPosition().z())
        );
    }

    private Vector3fc tryToMove(Vector3fc startPosition, Vector3fc endPosition) {
        List<Collision> collisions = getUniverse().getCollisions().collideSegment(new LineSegmentf(startPosition, endPosition));
        if (!collisions.isEmpty()) {
            Vector3fc intersection = collisions.get(0).getPosition();
            Vector3f shift = Maths.normalizeOrEmpty(new Vector3f(startPosition).sub(endPosition)).mul(2 * EPS, new Vector3f());
            return shift.add(intersection);
        }
        return endPosition;
    }

    private void checkInputs() {

        Vector3f move = new Vector3f(0);
        if (getUniverse().getEventSnapReceiver().isKeyDown(GLFW_KEY_W) || getUniverse().getEventSnapReceiver().isMouseDown(GLFW_MOUSE_BUTTON_1) && getUniverse().getEventSnapReceiver().isMouseDown(GLFW_MOUSE_BUTTON_2)) {
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
            setPosition(new Vector3f(50, 90, 50));
        }

        if (getUniverse().getEventSnapReceiver().isKeyDown(GLFW_KEY_SPACE)) {
            if (isOnTheGround()) {
                getCurrentVelocity().add(new Vector3f(0, jumpPower, 0));
            }
        }

        if (getUniverse().getEventSnapReceiver().isKeyDown(GLFW_KEY_B)) {
            getUniverse().getChunks().setBlock(getBlockPositionStandingOn(), Block.AIR);
            getUniverse().getChunks().recalculateBlockRegion(getBlockPositionStandingOn());
        }

    }


}
