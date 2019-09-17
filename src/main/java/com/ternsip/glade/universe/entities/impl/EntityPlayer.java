package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.network.ClientSide;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.collisions.base.Collision;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.Volumetric;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.protocol.PlayerActionPacket;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;
import java.util.List;

import static com.ternsip.glade.common.logic.Maths.*;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepository.MAX_LIGHT_LEVEL;
import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EntityPlayer extends Entity<EffigyBoy> {

    private static final float ARM_LENGTH = 5f;

    private transient final Timer blocksUpdateCheckTimer = new Timer(250);
    private transient final Callback<KeyEvent> keyCallback = this::handleKeyEvent;
    private transient boolean thirdPerson = false;
    private transient Vector3ic previousPosition = new Vector3i(-1000);

    @ServerSide
    private Vector3f currentVelocity = new Vector3f(0);

    @ClientSide
    private Vector3f moveEffort = new Vector3f(0);

    @ClientSide
    private float velocity = 0.1f;

    @ServerSide
    private float jumpPower = 0.3f;

    @ServerSide
    private boolean onTheGround = false;

    @ServerSide
    private float height = 2;

    @ClientSide
    private float cameraYRotation = 0;

    @ClientSide
    private LineSegmentf eyeSegment = new LineSegmentf();

    @ServerSide
    private float skyIntensity = 0;

    @Override
    public void onServerRegister() {
        super.onServerRegister();
        updateBlocksAround();
    }

    @Override
    public void onClientRegister() {
        super.onClientRegister();
        getUniverseClient().getEventSnapReceiver().registerCallback(KeyEvent.class, getKeyCallback());
        getUniverseClient().getEntityClientRepository().setCameraTarget(this);
    }

    @Override
    public void onClientUnregister() {
        super.onClientUnregister();
        getUniverseClient().getEventSnapReceiver().unregisterCallback(KeyEvent.class, getKeyCallback());
        getUniverseClient().getEntityClientRepository().setCameraTarget(new EntityDummy());
    }

    @Override
    public void update(EffigyBoy effigy) {
        super.update(effigy);
        setThirdPerson(effigy.getGraphics().getCameraController().isThirdPerson());
        setCameraYRotation(effigy.getGraphics().getCameraController().getRotation().y());
        if (!isThirdPerson()) {
            Vector3fc eye = effigy.getGraphics().getCameraController().getTarget();
            Vector3fc direction = effigy.getGraphics().getCameraController().getLookDirection().mul(ARM_LENGTH, new Vector3f());
            setEyeSegment(new LineSegmentf(eye, eye.add(direction, new Vector3f())));
        }
        effigy.setSkyIntensity(getSkyIntensity());
    }

    @Override
    public EffigyBoy getEffigy() {
        return new EffigyBoy();
    }

    @Override
    public void clientUpdate() {
        super.clientUpdate();
        Vector3f move = new Vector3f(0);
        setVisible(isThirdPerson());
        if (!isThirdPerson() || (getUniverseClient().getEventSnapReceiver().isMouseDown(GLFW_MOUSE_BUTTON_1) && getUniverseClient().getEventSnapReceiver().isMouseDown(GLFW_MOUSE_BUTTON_2))) {
            setRotation(new Vector3f(0, getCameraYRotation(), 0));
        }
        if (getUniverseClient().getEventSnapReceiver().isKeyDown(GLFW_KEY_W) || getUniverseClient().getEventSnapReceiver().isMouseDown(GLFW_MOUSE_BUTTON_1) && getUniverseClient().getEventSnapReceiver().isMouseDown(GLFW_MOUSE_BUTTON_2)) {
            move.add(FRONT_DIRECTION);
        }
        if (getUniverseClient().getEventSnapReceiver().isKeyDown(GLFW_KEY_S)) {
            move.add(BACK_DIRECTION);
        }
        if (getUniverseClient().getEventSnapReceiver().isKeyDown(GLFW_KEY_D)) {
            move.add(RIGHT_DIRECTION);
        }
        if (getUniverseClient().getEventSnapReceiver().isKeyDown(GLFW_KEY_A)) {
            move.add(LEFT_DIRECTION);
        }
        setMoveEffort(normalizeOrEmpty(move).mul(getVelocity(), new Vector3f()));
    }

    @Override
    public void serverUpdate() {
        Vector3f moveDirection = getMoveEffort().rotate(Maths.getRotationQuaternion(getRotation()), new Vector3f());
        getCurrentVelocity().add(getUniverseServer().getBalance().getGravity());
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
        Vector3ic blockPos = round(getPosition());
        setSkyIntensity(getUniverseServer().getBlocksRepository().isBlockExists(blockPos) ? getUniverseServer().getBlocksRepository().getSkyLight(blockPos) / (float) MAX_LIGHT_LEVEL : 1);
        if (getBlocksUpdateCheckTimer().isOver()) {
            updateBlocksAround();
            getBlocksUpdateCheckTimer().drop();
        }
    }

    @ServerSide
    public void setVolumetric(Volumetric volumetric) {
        getVolumetric().setPosition(volumetric.getPosition());
        getVolumetric().setScale(volumetric.getScale());
        getVolumetric().setLastTimeChanged(volumetric.getLastTimeChanged());
        getVolumetricInterpolated().updateWithVolumetric(getVolumetric());
    }

    @Override
    @ClientSide
    public void setRotation(Vector3fc rotation) {
        getVolumetric().setRotation(rotation);
    }

    @Override
    @ClientSide
    public void setVisible(boolean visible) {
        getVolumetric().setVisible(visible);
    }

    public Vector3i getBlockPositionStandingOn() {
        return new Vector3i(
                (int) Math.floor(getPosition().x()),
                (int) Math.floor(getPosition().y()) - 1,
                (int) Math.floor(getPosition().z())
        );
    }

    public void handleAction(Action action) {
        if (action == Action.RESPAWN) {
            setRotation(new Vector3f(0, 0, 0));
            setPosition(new Vector3f(50, 90, 50));
        }

        if (action == Action.TELEPORT_FAR) {
            setRotation(new Vector3f(0, 0, 0));
            setPosition(new Vector3f(512, 90, 512));
        }

        if (action == Action.JUMP) {
            if (isOnTheGround()) {
                getCurrentVelocity().add(new Vector3f(0, getJumpPower(), 0));
            }
        }

        if (action == Action.DESTROY_BLOCK_UNDER) {
            Vector3ic blockUnder = getBlockPositionStandingOn();
            if (getUniverseServer().getBlocksRepository().isBlockExists(blockUnder)) {
                getUniverseServer().getBlocksRepository().setBlock(blockUnder, Block.AIR);
            }
        }

        if (action == Action.DESTROY_SELECTED_BLOCK) {
            Vector3ic blockPositionLooking = getUniverseServer().getBlocksRepository().traverse(getEyeSegment(), (block) -> block != Block.AIR);
            if (blockPositionLooking != null && getUniverseServer().getBlocksRepository().isBlockExists(blockPositionLooking)) {
                getUniverseServer().getBlocksRepository().setBlock(blockPositionLooking, Block.AIR);
            }
        }
    }

    private Vector3fc tryToMove(Vector3fc startPosition, Vector3fc endPosition) {
        List<Collision> collisions = getUniverseServer().getCollisions().collideSegment(new LineSegmentf(startPosition, endPosition));
        if (!collisions.isEmpty()) {
            Vector3fc intersection = collisions.get(0).getPosition();
            Vector3f shift = Maths.normalizeOrEmpty(new Vector3f(startPosition).sub(endPosition)).mul(2 * EPS, new Vector3f());
            return shift.add(intersection);
        }
        return endPosition;
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_R && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, Action.RESPAWN));
        }

        if (event.getKey() == GLFW_KEY_T && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, Action.TELEPORT_FAR));
        }

        if (event.getKey() == GLFW_KEY_SPACE && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, Action.JUMP));
        }

        if (event.getKey() == GLFW_KEY_B && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, Action.DESTROY_BLOCK_UNDER));
        }

        if (event.getKey() == GLFW_KEY_Q && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, Action.DESTROY_SELECTED_BLOCK));
        }
    }

    private void updateBlocksAround() {
        Vector3ic newPos = new Vector3i((int) getPosition().x(), (int) getPosition().y(), (int) getPosition().z());
        if (!getPreviousPosition().equals(newPos)) {
            getUniverseServer().getBlocksRepository().processMovement(new Vector3i(getPreviousPosition()), newPos);
            setPreviousPosition(newPos);
        }
    }

    public enum Action {

        JUMP,
        RESPAWN,
        TELEPORT_FAR,
        DESTROY_BLOCK_UNDER,
        DESTROY_SELECTED_BLOCK,

    }

}
