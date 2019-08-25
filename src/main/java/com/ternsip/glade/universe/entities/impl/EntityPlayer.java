package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.universe.collisions.base.Collision;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;
import java.util.List;

import static com.ternsip.glade.common.logic.Maths.*;
import static com.ternsip.glade.universe.parts.chunks.Blocks.MAX_LIGHT_LEVEL;
import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EntityPlayer extends Entity<EffigyBoy> {

    private static final float ARM_LENGTH = 5f;

    private Vector3f currentVelocity = new Vector3f();
    private Vector3fc lookDirection = new Vector3f(0);
    private Vector3fc moveEffort = new Vector3f(0);
    private float velocity = 0.1f;
    private float jumpPower = 0.3f;
    private boolean onTheGround = false;
    private float height = 2;
    private final Callback<KeyEvent> keyCallback = this::handleKeyEvent;

    @Override
    public void register() {
        super.register();
        getUniverse().getEventSnapReceiver().registerCallback(KeyEvent.class, keyCallback);
    }

    @Override
    public void unregister() {
        super.unregister();
        getUniverse().getEventSnapReceiver().unregisterCallback(KeyEvent.class, keyCallback);
    }

    @Override
    public void update(EffigyBoy effigy) {
        super.update(effigy);
        if (!effigy.getGraphics().getCameraController().isThirdPerson()) {
            setLookDirection(effigy.getGraphics().getCameraController().getLookDirection());
        }
        effigy.setSkyIntensity(getSkyIntensity());
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
        setPosition(tryZ);
        setOnTheGround(tryToMove(cPos, new Vector3f(cPos).add(DOWN_DIRECTION)).equals(cPos, 5 * EPS));
        if (isOnTheGround()) {
            getCurrentVelocity().y = 0;
        }
    }

    @Override
    public Vector3f getCameraAttachmentPoint() {
        return super.getCameraAttachmentPoint().add(0, getHeight(), 0, new Vector3f());
    }

    public Vector3i getBlockPositionStandingOn() {
        return new Vector3i(
                (int) Math.floor(getPosition().x()),
                (int) Math.floor(getPosition().y()) - 1,
                (int) Math.floor(getPosition().z())
        );
    }

    private float getSkyIntensity() {
        Vector3ic blockPos = round(getPosition());
        return getUniverse().getBlocks().isBlockExists(blockPos) ? getUniverse().getBlocks().getSkyLight(blockPos) / (float) MAX_LIGHT_LEVEL : 1;
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

    }

    private void handleKeyEvent(KeyEvent event) {

        if (event.getKey() == GLFW_KEY_R && event.getAction() == GLFW_PRESS) {
            setRotation(new Vector3f(0, 0, 0));
            setPosition(new Vector3f(50, 90, 50));
        }

        if (event.getKey() == GLFW_KEY_T && event.getAction() == GLFW_PRESS) {
            setRotation(new Vector3f(0, 0, 0));
            setPosition(new Vector3f(512, 90, 512));
        }

        if (event.getKey() == GLFW_KEY_SPACE && event.getAction() == GLFW_PRESS) {
            if (isOnTheGround()) {
                getCurrentVelocity().add(new Vector3f(0, jumpPower, 0));
            }
        }

        if (event.getKey() == GLFW_KEY_B && event.getAction() == GLFW_PRESS) {
            Vector3ic blockUnder = getBlockPositionStandingOn();
            if (getUniverse().getBlocks().isBlockExists(blockUnder)) {
                getUniverse().getBlocks().setBlock(blockUnder, Block.AIR);
            }
        }

        if (event.getKey() == GLFW_KEY_Q && event.getAction() == GLFW_PRESS) {
            LineSegmentf lookingSegment = new LineSegmentf(
                    getCameraAttachmentPoint().x(), getCameraAttachmentPoint().y(), getCameraAttachmentPoint().z(),
                    getCameraAttachmentPoint().x() + lookDirection.x() * ARM_LENGTH,
                    getCameraAttachmentPoint().y() + lookDirection.y() * ARM_LENGTH,
                    getCameraAttachmentPoint().z() + lookDirection.z() * ARM_LENGTH
            );
            Vector3ic blockPositionLooking = getUniverse().getBlocks().traverse(lookingSegment, (block) -> block != Block.AIR);
            if (blockPositionLooking != null && getUniverse().getBlocks().isBlockExists(blockPositionLooking)) {
                getUniverse().getBlocks().setBlock(blockPositionLooking, Block.AIR);
            }
        }

    }

}
