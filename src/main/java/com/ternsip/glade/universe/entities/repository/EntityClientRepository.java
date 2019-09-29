package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.entities.base.EntityBase;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityClientRepository extends EntityRepository<EntityClient> implements IUniverseClient {

    @Getter(value = AccessLevel.PRIVATE)
    private final Timer networkTimer = new Timer(1000L / getUniverseClient().getBalance().getNetworkTicksPerSecond());

    private GraphicalEntity cameraTarget = null;

    public void update() {
        getUuidToEntity().values().forEach(EntityBase::update);
        if (getUniverseClient().getClient().getConnection().isActive() && getNetworkTimer().isOver()) {
            getUuidToEntity().values().forEach(EntityBase::networkUpdate);
            getNetworkTimer().drop();
        }
    }

}
