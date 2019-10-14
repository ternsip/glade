package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.collisions.base.Collision;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.GraphicalEntityServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.items.Inventory;
import com.ternsip.glade.universe.parts.items.ItemBlock;
import com.ternsip.glade.universe.parts.items.ItemSelectTool;
import com.ternsip.glade.universe.parts.player.PlayerAnimation;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.io.ObjectOutputStream;
import java.util.List;

import static com.ternsip.glade.common.logic.Maths.DOWN_DIRECTION;
import static com.ternsip.glade.common.logic.Maths.EPS;
import static com.ternsip.glade.universe.entities.ui.UIInventory.SELECTION_INVENTORY_SIZE;

@Getter
@Setter
public class EntityPlayerServer extends GraphicalEntityServer {

    private final Connection allowedConnection;
    private final Inventory selectionInventory = new Inventory(SELECTION_INVENTORY_SIZE);
    private transient Vector3ic previousPosition = new Vector3i(-1000);
    private Vector3f moveEffort = new Vector3f(0);
    private LineSegmentf eyeSegment = new LineSegmentf();
    private Vector3f currentVelocity = new Vector3f(0);
    private float jumpPower = 0.3f;
    private boolean onTheGround = false;
    private PlayerAnimation playerAnimation = PlayerAnimation.IDLE;

    public EntityPlayerServer(Connection allowedConnection) {
        this.allowedConnection = allowedConnection;
        this.selectionInventory.getItems()[0] = new ItemBlock(Block.STONE);
        this.selectionInventory.getItems()[0].setCount(999);
        this.selectionInventory.getItems()[1] = new ItemBlock(Block.WOOD);
        this.selectionInventory.getItems()[1].setCount(999);
        this.selectionInventory.getItems()[2] = new ItemBlock(Block.LEAVES);
        this.selectionInventory.getItems()[2].setCount(999);
        this.selectionInventory.getItems()[3] = new ItemSelectTool();
        this.selectionInventory.getItems()[4] = new ItemBlock(Block.LAVA);
        this.selectionInventory.getItems()[4].setCount(999);
    }

    @Override
    public void register() {
        super.register();
        updateBlocksAround();
    }

    @Override
    public void networkUpdate() {
        super.networkUpdate();
        updateBlocksAround();
    }

    @Override
    public EntityClient getEntityClient(Connection connection) {
        return connection == getAllowedConnection()
                ? new EntityPlayer(getSelectionInventory())
                : new EntityAnotherPlayer();
    }

    @Override
    public void update() {
        super.update();
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
        refreshPlayerAnimation();
    }

    private void refreshPlayerAnimation() {
        Vector3ic pos = getPreviousPosition();
        if (getUniverseServer().getBlocksRepository().isBlockExists(pos) && getUniverseServer().getBlocksRepository().getBlock(pos) == Block.WATER) {
            setPlayerAnimation(PlayerAnimation.FLOATING);
            return;
        }
        if (!isOnTheGround()) {
            setPlayerAnimation(PlayerAnimation.FALLING);
            return;
        }
        if (getMoveEffort().lengthSquared() > 1e-3f) {
            setPlayerAnimation(PlayerAnimation.RUN);
            return;
        }
        setPlayerAnimation(PlayerAnimation.IDLE);
    }

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {
        super.writeToStream(oos);
        oos.writeObject(getPlayerAnimation());
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

}
