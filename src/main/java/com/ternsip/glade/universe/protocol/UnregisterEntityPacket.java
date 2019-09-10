package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.Packet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class UnregisterEntityPacket extends Packet {

    private final UUID uuid;

    @Override
    public void apply(Connection connection) {
        getUniverse().getEntityClientRepository().unregister(getUuid());
    }

}
