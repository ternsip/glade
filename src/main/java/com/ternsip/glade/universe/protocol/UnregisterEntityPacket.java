package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class UnregisterEntityPacket extends ClientPacket {

    private final UUID uuid;

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getEntityClientRepository().unregister(getUuid());
    }

}
