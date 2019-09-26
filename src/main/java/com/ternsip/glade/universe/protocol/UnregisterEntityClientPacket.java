package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.EntityServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class UnregisterEntityClientPacket extends ClientPacket {

    private final UUID uuid;

    public UnregisterEntityClientPacket(EntityServer entityServer) {
        this.uuid = entityServer.getUuid();
    }

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getEntityClientRepository().unregister(getUuid());
    }

}
