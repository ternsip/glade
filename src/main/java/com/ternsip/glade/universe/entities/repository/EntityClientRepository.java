package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.visual.base.EffigyDummy;
import com.ternsip.glade.universe.entities.base.EntityBase;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.EntityGeneric;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.protocol.EntitiesStateServerPacket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityClientRepository extends EntityRepository<EntityClient> implements IUniverseClient {

    @Getter(value = AccessLevel.PRIVATE)
    private final Timer networkTimer = new Timer(50); // TODO get this value as a tickrate from options/balance

    private GraphicalEntity aim = new EntityGeneric(EffigyDummy::new);
    private GraphicalEntity cameraTarget = null;

    public void update() {
        getUuidToEntity().values().forEach(EntityBase::update);
        if (getNetworkTimer().isOver() && !getUuidToTransferable().isEmpty()) {
            getUniverseClient().getClient().send(new EntitiesStateServerPacket(getEntitiesState()));
            getNetworkTimer().drop();
        }
    }

}
