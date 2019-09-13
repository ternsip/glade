package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerPacket;
import com.ternsip.glade.universe.entities.repository.EntitiesChanges;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntitiesChangedServerPacket extends ServerPacket {

    private final EntitiesChanges changes;

    @Override
    public void apply(Connection connection) {
        getUniverseServer().getEntityServerRepository().applyEntitiesChanges(getChanges());
    }

}
