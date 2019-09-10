package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.Packet;
import com.ternsip.glade.universe.entities.repository.EntitiesChanges;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntitiesChangedServerPacket extends Packet {

    private final EntitiesChanges changes;

    @Override
    public void apply(Connection connection) {
        getUniverse().getEntityServerRepository().applyEntitiesChanges(getChanges());
    }

}
