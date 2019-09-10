package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.Packet;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class SunPacket implements Packet {

    private final UUID uuid;

    @Override
    public void apply(Connection connection) {
        EntitySun sun = (EntitySun) getUniverse().getEntityClientRepository().getUuidToEntity().get(getUuid());
        getUniverse().getEntityClientRepository().setSun(sun);
    }

}
