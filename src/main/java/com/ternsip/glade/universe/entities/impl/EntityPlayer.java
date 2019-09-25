package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.protocol.PlayerActionPacket;
import lombok.Getter;
import lombok.Setter;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.ternsip.glade.common.logic.Maths.*;
import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class EntityPlayer extends GraphicalEntity<EffigyBoy> {

    private static final float ARM_LENGTH = 5f;
    private transient final Callback<KeyEvent> keyCallback = this::handleKeyEvent;
    private transient boolean thirdPerson = false;

    private Vector3f moveEffort = new Vector3f(0);
    private float velocity = 0.1f;
    private float cameraYRotation = 0;
    private LineSegmentf eyeSegment = new LineSegmentf();

    private float skyIntensity = 0;

    @Override
    public void register() {
        super.register();
        getUniverseClient().getEventIOReceiver().registerCallback(KeyEvent.class, getKeyCallback());
        getUniverseClient().getEntityClientRepository().setCameraTarget(this);
    }

    @Override
    public void unregister() {
        super.unregister();
        getUniverseClient().getEventIOReceiver().unregisterCallback(KeyEvent.class, getKeyCallback());
        getUniverseClient().getEntityClientRepository().setCameraTarget(null);
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
    public void readFromStream(ObjectInputStream ois) throws Exception {
        setSkyIntensity(ois.readFloat());
        getVolumetricInterpolated().update(
                ois.readFloat(), ois.readFloat(), ois.readFloat(),
                getRotation().x(), getRotation().y(), getRotation().z(),
                ois.readFloat(), ois.readFloat(), ois.readFloat(),
                isVisible()
        );
    }

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {
        getMoveEffort().writeExternal(oos);
        oos.writeFloat(getVelocity());
        oos.writeFloat(getCameraYRotation());
        getEyeSegment().writeExternal(oos);
        oos.writeFloat(getRotation().x());
        oos.writeFloat(getRotation().y());
        oos.writeFloat(getRotation().z());
        oos.writeBoolean(isVisible());
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_R && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, EntityPlayerServer.Action.RESPAWN));
        }
        if (event.getKey() == GLFW_KEY_T && event.getAction() == GLFW_PRESS) {
            // TODO sometimes does not work by some reason
            getUniverseClient().getClient().send(new PlayerActionPacket(this, EntityPlayerServer.Action.TELEPORT_FAR));
        }
        if (event.getKey() == GLFW_KEY_SPACE && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, EntityPlayerServer.Action.JUMP));
        }
        if (event.getKey() == GLFW_KEY_B && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, EntityPlayerServer.Action.DESTROY_BLOCK_UNDER));
        }
        if (event.getKey() == GLFW_KEY_Q && event.getAction() == GLFW_PRESS) {
            getUniverseClient().getClient().send(new PlayerActionPacket(this, EntityPlayerServer.Action.DESTROY_SELECTED_BLOCK));
        }
    }

}
