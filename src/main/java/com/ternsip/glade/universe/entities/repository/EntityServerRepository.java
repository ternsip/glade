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
import com.ternsip.glade.universe.protocol.InitiateConnectionPacket;
import com.ternsip.glade.universe.protocol.RegisterEntityPacket;
import com.ternsip.glade.universe.protocol.UnregisterEntityPacket;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter(value = AccessLevel.PRIVATE)
public class EntityServerRepository extends EntityRepository<EntityServer> implements IUniverseServer {

    private final Timer networkTimer = new Timer(50); // TODO get this value as a tickrate from options/balance
    private final Set<Connection> initiatedConnections = new HashSet<>();

    private Callback<OnClientConnect> onClientConnectCallback = this::onClientConnect;
    private Callback<OnClientDisconnect> onClientDisconnectCallback = this::onClientDisconnect;

    public EntityServerRepository() {
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    @Override
    public <T extends EntityServer> void register(T entity) {
        super.register(entity);
        if (entity instanceof Obstacle) {
            getUniverseServer().getCollisions().add((Obstacle) entity);
        }
        if (entity.isTransferable()) {
            getInitiatedConnections().forEach(connection -> getUniverseServer().getServer().send(new RegisterEntityPacket(entity), connection));
            registerTransferable(entity);
        }
    }

    @Override
    public <T extends EntityServer> void unregister(T entity) {
        super.unregister(entity);
        if (entity instanceof Obstacle) {
            getUniverseServer().getCollisions().remove((Obstacle) entity);
        }
        if (entity.isTransferable()) {
            unregisterTransferable(entity.getUuid());
            getInitiatedConnections().forEach(connection -> getUniverseServer().getServer().send(new UnregisterEntityPacket(entity.getUuid()), connection));
        }
    }

    @Override
    public void finish() {
        super.finish();
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    public void update() {
        getUuidToEntity().values().forEach(EntityBase::update);
        if (getNetworkTimer().isOver() && !getUuidToTransferable().isEmpty()) {
            getUniverseServer().getServer().send(new EntitiesStateClientPacket(getEntitiesState()), connection -> getInitiatedConnections().contains(connection));
            getNetworkTimer().drop();
        }
    }

    private void onClientConnect(OnClientConnect onClientConnect) {
        Collection<RegisterEntityPacket> subPackets = getUuidToTransferable().values().stream().map(RegisterEntityPacket::new).collect(Collectors.toList());
        getUniverseServer().getServer().send(new InitiateConnectionPacket(subPackets), connection -> connection == onClientConnect.getConnection());
        getInitiatedConnections().add(onClientConnect.getConnection());
    }

    private void onClientDisconnect(OnClientDisconnect onClientDisconnect) {
        getInitiatedConnections().remove(onClientDisconnect.getConnection());
    }



}
