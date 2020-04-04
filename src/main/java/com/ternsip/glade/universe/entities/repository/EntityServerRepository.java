package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.events.network.OnClientDisconnect;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.entities.base.EntityBase;
import com.ternsip.glade.universe.entities.base.EntityServer;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import com.ternsip.glade.universe.protocol.EntitiesStateClientPacket;
import com.ternsip.glade.universe.protocol.InitiateConnectionClientPacket;
import com.ternsip.glade.universe.protocol.RegisterEntityClientPacket;
import com.ternsip.glade.universe.protocol.UnregisterEntityClientPacket;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Getter(value = AccessLevel.PRIVATE)
public class EntityServerRepository extends EntityRepository<EntityServer> implements IUniverseServer {

    private final Timer networkTimer = new Timer(1000L / getUniverseServer().getBalance().getNetworkTicksPerSecond());
    private final Set<Connection> initiatedConnections = new HashSet<>();

    private Callback<OnClientConnect> onClientConnectCallback = this::onClientConnect;
    private Callback<OnClientDisconnect> onClientDisconnectCallback = this::onClientDisconnect;

    public EntityServerRepository() {
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    @Override
    public synchronized <T extends EntityServer> void register(T entity) {
        super.register(entity);
        if (entity instanceof Obstacle) {
            getUniverseServer().getCollisionsServer().add((Obstacle) entity);
        }
        if (entity.isTransferable()) {
            getInitiatedConnections().forEach(connection -> getUniverseServer().getServer().send(new RegisterEntityClientPacket(entity, connection), connection));
        }
    }

    @Override
    public synchronized <T extends EntityServer> void unregister(T entity) {
        super.unregister(entity);
        if (entity instanceof Obstacle) {
            getUniverseServer().getCollisionsServer().remove((Obstacle) entity);
        }
        if (entity.isTransferable()) {
            getUniverseServer().getServer().send(new UnregisterEntityClientPacket(entity), getConnectionInitiatedCondition());
        }
    }

    @Override
    public void finish() {
        super.finish();
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    public synchronized void update() {
        getUuidToEntity().values().forEach(EntityBase::update);
        if (getUniverseServer().getServer().getServerHolder().isActive() && getNetworkTimer().isOver()) {
            getUniverseServer().getServer().send(new EntitiesStateClientPacket(getUuidToEntity().values()), getConnectionInitiatedCondition());
            getUuidToEntity().values().forEach(EntityBase::networkUpdate);
            getNetworkTimer().drop();
        }
    }

    public Function<Connection, Boolean> getConnectionInitiatedCondition() {
        return connection -> getInitiatedConnections().contains(connection);
    }

    private synchronized void onClientConnect(OnClientConnect onClientConnect) {
        getUniverseServer().getServer().send(new InitiateConnectionClientPacket(getUuidToEntity().values(), onClientConnect.getConnection()), onClientConnect.getConnection());
        getInitiatedConnections().add(onClientConnect.getConnection());
    }

    private synchronized void onClientDisconnect(OnClientDisconnect onClientDisconnect) {
        getInitiatedConnections().remove(onClientDisconnect.getConnection());
    }

}
