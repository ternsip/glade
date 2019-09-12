package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class CameraTargetPacket extends ClientPacket {

    private final UUID uuid;

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getEntityClientRepository().setCameraTarget(getUniverseClient().getEntityClientRepository().getUuidToEntity().get(getUuid()));
    }

}
