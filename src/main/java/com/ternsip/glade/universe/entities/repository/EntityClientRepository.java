package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntityDummy;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.protocol.EntitiesChangedServerPacket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityClientRepository extends EntityRepository implements IUniverseClient {

    private final FieldBuffer fieldBuffer = new FieldBuffer(false);
    private final Timer networkTimer = new Timer(50); // TODO get this value as a tickrate from options/balance

    private Entity aim = new EntityDummy();
    private Entity cameraTarget = new EntityDummy();

    public void update() {
        getUuidToEntity().values().forEach(Entity::clientUpdate);
        if (getNetworkTimer().isOver()) {
            EntitiesChanges entitiesChanges = findEntitiesChanges();
            if (!entitiesChanges.isEmpty()) {
                getUniverseClient().getClient().send(new EntitiesChangedServerPacket(entitiesChanges));
            }
            getNetworkTimer().drop();
        }
    }

}
