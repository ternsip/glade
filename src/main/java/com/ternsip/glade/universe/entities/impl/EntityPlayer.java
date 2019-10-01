package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.common.events.display.MouseButtonEvent;
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
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.ObjectInputStream;

import static com.ternsip.glade.common.logic.Maths.*;
import static org.lwjgl.glfw.GLFW.*;

@NoArgsConstructor
@Getter
@Setter
public class EntityPlayer extends GraphicalEntity<EffigyBoy> {

    private transient final Callback<KeyEvent> keyCallback = this::handleKeyEvent;
    private transient final Callback<MouseButtonEvent> mouseButtonEventCallback = this::handleMouseButtonEvent;
    private transient boolean thirdPerson = false;

    private Vector3f moveEffort = new Vector3f(0);
    private float velocity = 0.1f;
    private float cameraYRotation = 0;
    private LineSegmentf eyeSegment = new LineSegmentf();
    private Inventory selectionInventory;

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
    }

    @Override
    public void networkUpdate() {
        super.networkUpdate();
        getUniverseClient().getClient().send(new PlayerStateServerPacket(getUuid(), getMoveEffort(), getEyeSegment(), new Vector3f(getRotation())));
    }

    @Override
    public void update(EffigyBoy effigy) {
        super.update(effigy);
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
        boolean visible = ois.readBoolean();
        float skyIntensity = ois.readFloat();
        float emitIntensity = ois.readFloat();
        getVolumetricInterpolated().update(
                px, py, pz,
                getRotation().x(), getRotation().y(), getRotation().z(),
                sx, sy, sz,
                isVisible(),
                skyIntensity,
                emitIntensity
        );
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_R && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionServerPacket(getUuid(), new RespawnAction()));
        }
        if (event.getKey() == GLFW_KEY_T && event.getAction() == GLFW_PRESS) {
            // TODO sometimes pressing does not work by some reason (not sure)
            getUniverseClient().getClient().send(new PlayerActionServerPacket(getUuid(), new TeleportFarAction()));
        }
        if (event.getKey() == GLFW_KEY_SPACE && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionServerPacket(getUuid(), new JumpAction()));
        }
        if (event.getKey() == GLFW_KEY_B && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionServerPacket(getUuid(), new DestroyBlockUnderAction()));
        }
        if (event.getKey() == GLFW_KEY_Q && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionServerPacket(getUuid(), new DestroySelectedBlockAction()));
        }
    }

    private void handleMouseButtonEvent(MouseButtonEvent event) {
        if (event.getButton() == GLFW_MOUSE_BUTTON_2 && event.getAction() == GLFW_PRESS) {
            int slot = getUniverseClient().getEntityClientRepository().getEntityByClass(UIInventory.class).getCellSelected();
            getUniverseClient().getClient().send(new PlayerActionServerPacket(getUuid(), new UseItemAction(slot)));
        }
    }


}
