package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.repository.EntityRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntitiesStateClientPacket extends ClientPacket {

    private final EntityRepository.EntitiesState entitiesState;

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getEntityClientRepository().applyEntitiesState(getEntitiesState());
    }

}
