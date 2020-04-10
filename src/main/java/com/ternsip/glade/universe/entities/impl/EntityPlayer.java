package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.common.events.display.MouseButtonEvent;
import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.graphics.camera.CameraController;
import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.entities.ui.UIInventory;
import com.ternsip.glade.universe.parts.items.Inventory;
import com.ternsip.glade.universe.parts.player.*;
import com.ternsip.glade.universe.protocol.PlayerActionServerPacket;
import com.ternsip.glade.universe.protocol.PlayerStateServerPacket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.*;

import java.io.ObjectInputStream;
import java.util.Arrays;

import static com.ternsip.glade.common.logic.Maths.*;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.SIZE_X;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.SIZE_Z;
import static org.lwjgl.glfw.GLFW.*;

@NoArgsConstructor
@Getter
@Setter
public class EntityPlayer extends GraphicalEntity<EffigyBoy> {

    private static final int VIEW_DISTANCE = 128;
    private transient final Callback<KeyEvent> keyCallback = this::handleKeyEvent;
    private transient final Callback<MouseButtonEvent> mouseButtonEventCallback = this::handleMouseButtonEvent;
    private transient boolean thirdPerson = false;

    private Vector3f moveEffort = new Vector3f(0);
    private float velocity = 0.1f;
    private float cameraYRotation = 0;
    private LineSegmentf eyeSegment = new LineSegmentf();
    private Inventory selectionInventory;
    private Vector3ic previousPosition = new Vector3i(-1000, -1000, -1000);
    private PlayerAnimation playerAnimation = PlayerAnimation.IDLE;

    public EntityPlayer(Inventory selectionInventory) {
        this.selectionInventory = selectionInventory;
    }

    @Override
    public void register() {
        super.register();
        getUniverseClient().getEventIOReceiver().registerCallback(KeyEvent.class, getKeyCallback());
        getUniverseClient().getEventIOReceiver().registerCallback(MouseButtonEvent.class, getMouseButtonEventCallback());
        getUniverseClient().getEntityClientRepository().setCameraTarget(this);
        getUniverseClient().getEntityClientRepository().getEntityByClass(UIInventory.class).updateSelectionInventory(getSelectionInventory());
    }

    @Override
    public void unregister() {
        super.unregister();
        getUniverseClient().getEventIOReceiver().unregisterCallback(KeyEvent.class, getKeyCallback());
        getUniverseClient().getEventIOReceiver().unregisterCallback(MouseButtonEvent.class, getMouseButtonEventCallback());
        getUniverseClient().getEntityClientRepository().setCameraTarget(null);
    }

    @Override
    public void update() {
        super.update();
        Vector3f move = new Vector3f(0);
        setVisible(isThirdPerson());
        if (!isThirdPerson() || (getUniverseClient().getEventIOReceiver().isMouseDown(GLFW_MOUSE_BUTTON_1) && getUniverseClient().getEventIOReceiver().isMouseDown(GLFW_MOUSE_BUTTON_2))) {
            setRotation(new Vector3f(0, getCameraYRotation(), 0));
        }
        if (getUniverseClient().getEventIOReceiver().isKeyDown(GLFW_KEY_W) || getUniverseClient().getEventIOReceiver().isMouseDown(GLFW_MOUSE_BUTTON_1) && getUniverseClient().getEventIOReceiver().isMouseDown(GLFW_MOUSE_BUTTON_2)) {
            move.add(FRONT_DIRECTION);
        }
        if (getUniverseClient().getEventIOReceiver().isKeyDown(GLFW_KEY_S)) {
            move.add(BACK_DIRECTION);
        }
        if (getUniverseClient().getEventIOReceiver().isKeyDown(GLFW_KEY_D)) {
            move.add(RIGHT_DIRECTION);
        }
        if (getUniverseClient().getEventIOReceiver().isKeyDown(GLFW_KEY_A)) {
            move.add(LEFT_DIRECTION);
        }
        setMoveEffort(normalizeOrEmpty(move).mul(getVelocity(), new Vector3f()));
        updateBlocksAround();
    }

    @Override
    public void networkUpdate() {
        super.networkUpdate();
        getUniverseClient().getClient().send(new PlayerStateServerPacket(getUuid(), getMoveEffort(), getEyeSegment(), new Vector3f(getRotation())));
    }

    @Override
    public void update(EffigyBoy effigy) {
        super.update(effigy);
        if (!effigy.getAnimation().getAnimationTrack().getName().equalsIgnoreCase(getPlayerAnimation().name())) {
            effigy.getAnimation().play(getPlayerAnimation().name().toLowerCase());
        }
        CameraController cameraController = effigy.getGraphics().getCameraController();
        setThirdPerson(cameraController.isThirdPerson());
        setCameraYRotation(cameraController.getRotation().y());
        if (!isThirdPerson()) {
            Vector3fc eye = cameraController.getTarget();
            Vector3fc direction = cameraController.getLookDirection().mul(getUniverseClient().getBalance().getPlayerArmLength(), new Vector3f());
            setEyeSegment(new LineSegmentf(eye, eye.add(direction, new Vector3f())));
        }
    }

    @Override
    public EffigyBoy getEffigy() {
        return new EffigyBoy();
    }

    @Override
    public void readFromStream(ObjectInputStream ois) throws Exception {
        float px = ois.readFloat();
        float py = ois.readFloat();
        float pz = ois.readFloat();
        float rx = ois.readFloat();
        float ry = ois.readFloat();
        float rz = ois.readFloat();
        float sx = ois.readFloat();
        float sy = ois.readFloat();
        float sz = ois.readFloat();
        getVolumetricInterpolated().update(px, py, pz, getRotation().x(), getRotation().y(), getRotation().z(), sx, sy, sz);
        setPlayerAnimation((PlayerAnimation) ois.readObject());
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_R && event.getAction() == GLFW_PRESS) {
            applyAction(new RespawnAction());
        }
        if (event.getKey() == GLFW_KEY_T && event.getAction() == GLFW_PRESS) {
            // TODO sometimes pressing does not work by some reason (not sure), Update: this might be fixed (multithreading)
            applyAction(new TeleportFarAction());
        }
        if (event.getKey() == GLFW_KEY_Z && event.getAction() == GLFW_PRESS) {
            applyAction(new TeleportZeroAction());
        }
        if (event.getKey() == GLFW_KEY_SPACE && event.getAction() == GLFW_PRESS) {
            applyAction(new JumpAction());
        }
        if (event.getKey() == GLFW_KEY_B && event.getAction() == GLFW_PRESS) {
            applyAction(new DestroyBlockUnderAction());
        }
        if (event.getKey() == GLFW_KEY_Q && event.getAction() == GLFW_PRESS) {
            applyAction(new DestroySelectedBlockAction());
        }
    }

    private void handleMouseButtonEvent(MouseButtonEvent event) {
        if (event.getButton() == GLFW_MOUSE_BUTTON_2 && event.getAction() == GLFW_PRESS) {
            int slot = getUniverseClient().getEntityClientRepository().getEntityByClass(UIInventory.class).getCellSelected();
            applyAction(new UseItemAction(slot));
        }
    }

    private void applyAction(BaseAction baseAction) {
        baseAction.applyOnClient(this);
        getUniverseClient().getClient().send(new PlayerActionServerPacket(getUuid(), baseAction));
    }

    private void updateBlocksAround() {
        Vector3ic newPos = new Vector3i((int) getPosition().x(), (int) getPosition().y(), (int) getPosition().z());
        if (!getPreviousPosition().equals(newPos)) {
            processMovement(new Vector3i(getPreviousPosition()), newPos);
            setPreviousPosition(newPos);
        }
    }

    private void processMovement(Vector3ic prevPos, Vector3ic nextPos) {
        int sx = prevPos.x() - VIEW_DISTANCE;
        int sz = prevPos.z() - VIEW_DISTANCE;
        int ex = prevPos.x() + VIEW_DISTANCE;
        int ez = prevPos.z() + VIEW_DISTANCE;
        int sx2 = nextPos.x() - VIEW_DISTANCE;
        int sz2 = nextPos.z() - VIEW_DISTANCE;
        int ex2 = nextPos.x() + VIEW_DISTANCE;
        int ez2 = nextPos.z() + VIEW_DISTANCE;
        int[] ax = new int[]{sx, ex, sx2, ex2};
        int[] az = new int[]{sz, ez, sz2, ez2};
        Arrays.sort(ax);
        Arrays.sort(az);
        visualUpdate(ax[0], sz2, ax[1], ez2, sx2 < sx);
        visualUpdate(ax[2], sz2, ax[3], ez2, sx2 > sx);
        if (!(sx2 > ex) && !(ex2 < sx)) {
            visualUpdate(ax[0], az[1], ax[1], az[2], sz2 < sz);
            visualUpdate(ax[2], az[1], ax[3], az[2], sz2 > sz);
        }
    }

    private void visualUpdate(int x, int z, int ex, int ez, boolean additive) {
        int nx = Maths.clamp(x, 0, SIZE_X);
        int nz = Maths.clamp(z, 0, SIZE_Z);
        int nex = Maths.clamp(ex, 0, SIZE_X);
        int nez = Maths.clamp(ez, 0, SIZE_Z);
        int sizeX = nex - nx;
        int sizeZ = nez - nz;
        if (sizeX <= 0 || sizeZ <= 0) {
            return;
        }
        //getUniverseClient().getBlocksClientRepository().visualUpdate(new Vector3i(nx, 0, nz), new Vector3i(sizeX, SIZE_Y, sizeZ), additive);
    }

/*    private void processMovement(Vector3ic prevPos, Vector3ic nextPos) {
        int middlePrevChunkX = prevPos.x() / VISUAL_UPDATE_SIZE;
        int middlePrevChunkZ = prevPos.z() / VISUAL_UPDATE_SIZE;
        int startPrevChunkX = middlePrevChunkX - VIEW_DISTANCE;
        int startPrevChunkZ = middlePrevChunkZ - VIEW_DISTANCE;
        int endPrevChunkX = middlePrevChunkX + VIEW_DISTANCE;
        int endPrevChunkZ = middlePrevChunkZ + VIEW_DISTANCE;

        int middleNextChunkX = nextPos.x() / VISUAL_UPDATE_SIZE;
        int middleNextChunkZ = nextPos.z() / VISUAL_UPDATE_SIZE;
        int startNextChunkX = middleNextChunkX - VIEW_DISTANCE;
        int startNextChunkZ = middleNextChunkZ - VIEW_DISTANCE;
        int endNextChunkX = middleNextChunkX + VIEW_DISTANCE;
        int endNextChunkZ = middleNextChunkZ + VIEW_DISTANCE;

        if (startNextChunkX == startPrevChunkX && startNextChunkZ == startPrevChunkZ) {
            return;
        }
        requestBlockUpdates(startNextChunkX, startNextChunkZ, endNextChunkX, endNextChunkZ, startPrevChunkX, startPrevChunkZ, endPrevChunkX, endPrevChunkZ, true);
        requestBlockUpdates(startPrevChunkX, startPrevChunkZ, endPrevChunkX, endPrevChunkZ, startNextChunkX, startNextChunkZ, endNextChunkX, endNextChunkZ, false);
    }

    private void requestBlockUpdates(
            int startNextChunkX, int startNextChunkZ, int endNextChunkX, int endNextChunkZ,
            int startPrevChunkX, int startPrevChunkZ, int endPrevChunkX, int endPrevChunkZ,
            boolean additive
    ) {
        if (startNextChunkX >= CHUNKS_X || startNextChunkZ >= CHUNKS_Z || endNextChunkX < 0 || endNextChunkZ < 0) {
            return;
        }
        int scx = Maths.clamp(0, CHUNKS_X - 1, startNextChunkX);
        int scz = Maths.clamp(0, CHUNKS_Z - 1, startNextChunkZ);
        int ecx = Maths.clamp(0, CHUNKS_X - 1, endNextChunkX);
        int ecz = Maths.clamp(0, CHUNKS_Z - 1, endNextChunkZ);
        for (int cx = scx; cx <= ecx; ++cx) {
            for (int cz = scz; cz <= ecz; ++cz) {
                if (cx < startPrevChunkX || cx > endPrevChunkX || cz < startPrevChunkZ || cz > endPrevChunkZ) {
                    getUniverseClient().getBlocksClientRepository().visualUpdate(
                            new Vector3i(cx * VISUAL_UPDATE_SIZE, 0, cz * VISUAL_UPDATE_SIZE),
                            new Vector3i(VISUAL_UPDATE_SIZE, SIZE_Y, VISUAL_UPDATE_SIZE),
                            additive
                    );
                }
            }
        }
    }*/

}
