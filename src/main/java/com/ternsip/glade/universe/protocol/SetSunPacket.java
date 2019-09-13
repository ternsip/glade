package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class SetSunPacket extends ClientPacket {

    private final UUID uuid;

    @Override
    public void apply(Connection connection) {
        EntitySun sun = (EntitySun) getUniverseClient().getEntityClientRepository().getUuidToEntity().get(getUuid());
        getUniverseClient().getEntityClientRepository().setSun(sun);
    }

}
