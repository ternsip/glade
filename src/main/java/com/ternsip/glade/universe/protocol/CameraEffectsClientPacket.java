package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.impl.EntityCameraEffects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CameraEffectsClientPacket extends ClientPacket {

    private final boolean underWater;

    @Override
    public void apply(Connection connection) {
        EntityCameraEffects cameraEffects = getUniverseClient().getEntityClientRepository().getEntityByClass(EntityCameraEffects.class);
        cameraEffects.setUnderWater(isUnderWater());
    }

}
