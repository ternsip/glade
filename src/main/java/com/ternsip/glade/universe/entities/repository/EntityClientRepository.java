package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntityDummy;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import com.ternsip.glade.universe.protocol.EntitiesChangedServerPacket;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EntityClientRepository extends EntityRepository {

    private final FieldBuffer fieldBuffer = new FieldBuffer(false);
    private final Timer networkTimer = new Timer(150);

    private Entity aim = new EntityDummy();
    private Entity cameraTarget = new EntityDummy();
    private EntitySun sun = new EntitySun();

    public void register(Entity entity) {
        getUuidToEntity().put(entity.getUuid(), entity);
    }

    public void unregister(UUID uuid) {
        getUuidToEntity().remove(uuid);
    }

    public void update() {
        getUuidToEntity().values().forEach(Entity::clientUpdate);
        if (getNetworkTimer().isOver()) {
            EntitiesChanges entitiesChanges = findEntitiesChanges();
            if (!entitiesChanges.isEmpty()) {
                getUniverse().getClient().send(new EntitiesChangedServerPacket(entitiesChanges));
            }
            getNetworkTimer().drop();
        }
    }

}
