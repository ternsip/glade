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

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class EntityClientRepository extends EntityRepository<EntityClient> implements IUniverseClient {

    @Getter(value = AccessLevel.PRIVATE)
    private final ConcurrentHashMap<UUID, GraphicalEntity> uuidToGraphicalEntity = new ConcurrentHashMap<>();

    @Getter(value = AccessLevel.PRIVATE)
    private final Timer networkTimer = new Timer(50); // TODO get this value as a tickrate from options/balance

    private GraphicalEntity aim = new EntityGeneric(EffigyDummy::new);
    private GraphicalEntity cameraTarget = null;

    @Override
    public <T extends EntityClient> void register(T entity) {
        super.register(entity);
        if (entity instanceof GraphicalEntity) {
            getUuidToGraphicalEntity().put(entity.getUuid(), (GraphicalEntity) entity);
        }
    }

    @Override
    public <T extends EntityClient> void unregister(T entity) {
        super.unregister(entity);
        if (entity instanceof GraphicalEntity) {
            getUuidToGraphicalEntity().remove(entity.getUuid());
        }
    }

    public void update() {
        getUuidToEntity().values().forEach(EntityBase::update);
        if (getNetworkTimer().isOver() && !getUuidToTransferable().isEmpty()) {
            getUniverseClient().getClient().send(new EntitiesStateServerPacket(getEntitiesState()));
            getNetworkTimer().drop();
        }
    }

    public Collection<GraphicalEntity> getGraphicalEntities() {
        return getUuidToGraphicalEntity().values();
    }

}
