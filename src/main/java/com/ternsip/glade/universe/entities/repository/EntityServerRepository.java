package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import com.ternsip.glade.universe.protocol.EntitiesChangedClientPacket;
import com.ternsip.glade.universe.protocol.RegisterEntityPacket;
import com.ternsip.glade.universe.protocol.SetSunPacket;
import com.ternsip.glade.universe.protocol.UnregisterEntityPacket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityServerRepository extends EntityRepository implements IUniverseServer {

    private final FieldBuffer fieldBuffer = new FieldBuffer(true);
    private final Timer networkTimer = new Timer(50); // TODO get this value as a tickrate from options/balance

    private EntitySun sun = new EntitySun();
    private Callback<OnClientConnect> onClientConnectCallback = this::onClientConnectToServer;

    public EntityServerRepository() {
        getUniverseServer().getServer().getNetworkServerEventReceiver().registerCallback(OnClientConnect.class, getOnClientConnectCallback());
    }

    public void register(Entity entity) {
        getUuidToEntity().put(entity.getUuid(), entity);
        if (entity instanceof Obstacle) {
            getUniverseServer().getCollisions().add((Obstacle) entity);
        }
        getUniverseServer().getServer().sendAll(new RegisterEntityPacket(entity));
    }

    public void unregister(Entity entity) {
        getUuidToEntity().remove(entity.getUuid());
        if (entity instanceof Obstacle) {
            getUniverseServer().getCollisions().remove((Obstacle) entity);
        }
        getUniverseServer().getServer().sendAll(new UnregisterEntityPacket(entity.getUuid()));
    }

    public void update() {
        getUuidToEntity().values().forEach(Entity::serverUpdate);
        if (getNetworkTimer().isOver()) {
            EntitiesChanges entitiesChanges = findEntitiesChanges();
            if (!entitiesChanges.isEmpty()) {
                getUniverseServer().getServer().sendAll(new EntitiesChangedClientPacket(entitiesChanges));
            }
            getNetworkTimer().drop();
        }
    }

    public void finish() {
        getUniverseServer().getServer().getNetworkServerEventReceiver().unregisterCallback(OnClientConnect.class, getOnClientConnectCallback());
    }

    private void onClientConnectToServer(OnClientConnect onClientConnect) {
        // TODO put this into one packet
        for (Entity entity : getUuidToEntity().values()) {
            // TODO send only specific client
            getUniverseServer().getServer().sendAll(new RegisterEntityPacket(entity));
        }
        getUniverseServer().getServer().sendAll(new SetSunPacket(sun.getUuid()));
    }

    public void setSun(EntitySun sun) {
        this.sun = sun;
        getUniverseServer().getServer().sendAll(new SetSunPacket(sun.getUuid()));
    }
}
