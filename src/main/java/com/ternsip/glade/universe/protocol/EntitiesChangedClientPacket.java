package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.repository.EntitiesChanges;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntitiesChangedClientPacket extends ClientPacket {

    private final EntitiesChanges changes;

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getEntityClientRepository().applyEntitiesChanges(getChanges());
    }

}
