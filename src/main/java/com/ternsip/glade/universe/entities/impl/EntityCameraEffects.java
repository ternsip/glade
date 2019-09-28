package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.visual.base.EffigyDummy;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.protocol.CameraEffectsServerPacket;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@Getter
@Setter
public class EntityCameraEffects extends GraphicalEntity<EffigyDummy> {

    private transient final Timer stateSenderTimer = new Timer(50); // TODO get this value as a tickrate from options/balance
    private final Vector3f cameraPosition = new Vector3f(0);
    private boolean underWater = false;

    @Override
    public void update() {
        super.update();
        if (getStateSenderTimer().isOver()) {
            getUniverseClient().getClient().send(new CameraEffectsServerPacket(new Vector3f(getCameraPosition())));
            getStateSenderTimer().drop();
        }
    }

    @Override
    public void update(EffigyDummy effigy) {
        super.update(effigy);
        getCameraPosition().set(effigy.getGraphics().getCamera().getPosition());
    }

    @Override
    public EffigyDummy getEffigy() {
        return new EffigyDummy();
    }

}
