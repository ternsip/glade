package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.EffigyDummy;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.protocol.CameraEffectsServerPacket;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@Getter
@Setter
public class EntityCameraEffects extends GraphicalEntity<EffigyDummy> {

    private final Vector3f cameraTargetPosition = new Vector3f(0);
    private final Vector3f cameraPosition = new Vector3f(0);

    private boolean underWater = false;

    private float cameraDistanceFix = Float.MAX_VALUE;

    @Override
    public void networkUpdate() {
        super.networkUpdate();
        getUniverseClient().getClient().send(new CameraEffectsServerPacket(new Vector3f(getCameraTargetPosition()), new Vector3f(getCameraPosition())));
    }

    @Override
    public void update(EffigyDummy effigy) {
        super.update(effigy);
        getCameraPosition().set(effigy.getGraphics().getCamera().getPosition());
        getCameraTargetPosition().set(effigy.getGraphics().getCameraController().getTarget());
        effigy.getGraphics().getCameraController().setDistanceFix(getCameraDistanceFix());
    }

    @Override
    public EffigyDummy getEffigy() {
        return new EffigyDummy();
    }

}
