package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerPacket;
import com.ternsip.glade.universe.entities.repository.EntityRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntitiesStateServerPacket extends ServerPacket {

    private final EntityRepository.EntitiesState entitiesState;

    @Override
    public void apply(Connection connection) {
        getUniverseServer().getEntityServerRepository().applyEntitiesState(getEntitiesState());
    }

}
