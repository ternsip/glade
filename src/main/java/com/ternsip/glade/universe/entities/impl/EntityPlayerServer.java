package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.collisions.base.Collision;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.GraphicalEntityServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.*;

import java.io.ObjectOutputStream;
import java.lang.Math;
import java.util.List;

import static com.ternsip.glade.common.logic.Maths.*;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepository.MAX_LIGHT_LEVEL;

@RequiredArgsConstructor
@Getter
@Setter
public class EntityPlayerServer extends GraphicalEntityServer {

    private transient final Timer blocksUpdateCheckTimer = new Timer(250);
    private transient Vector3ic previousPosition = new Vector3i(-1000);

    private final Connection allowedConnection;

    private Vector3f moveEffort = new Vector3f(0);
    private LineSegmentf eyeSegment = new LineSegmentf();

    private Vector3f currentVelocity = new Vector3f(0);
    private float jumpPower = 0.3f;
    private boolean onTheGround = false;
    private float skyIntensity = 0;

    @Override
    public void register() {
        super.register();
        updateBlocksAround();
    }

    @Override
    public void update() {
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

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {
        super.writeToStream(oos);
        oos.writeFloat(getSkyIntensity());
    }

    @Override
    public EntityClient getEntityClient(Connection connection) {
        return connection == getAllowedConnection() ? new EntityPlayer() : new EntityAnotherPlayer();
    }

    private Vector3i getBlockPositionStandingOn() {
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
