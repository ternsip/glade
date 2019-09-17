package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.events.network.OnClientDisconnect;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.NetworkSide;
import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntityDummy;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import com.ternsip.glade.universe.protocol.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class EntityServerRepository extends EntityRepository implements IUniverseServer {

    private final FieldBuffer fieldBuffer = new FieldBuffer(true);
    private final Timer networkTimer = new Timer(50); // TODO get this value as a tickrate from options/balance
    private final Set<Connection> initiatedConnections = new HashSet<>();

    private Entity cameraTarget = new EntityDummy();
    private Callback<OnClientConnect> onClientConnectCallback = this::onClientConnect;
    private Callback<OnClientDisconnect> onClientDisconnectCallback = this::onClientDisconnect;

    public EntityServerRepository() {
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    @Override
    public void register(Entity entity) {
        super.register(entity);
        if (entity instanceof Obstacle) {
            getUniverseServer().getCollisions().add((Obstacle) entity);
        }
        if (entity.getNetworkExpectedSide() != NetworkSide.SERVER) {
            getUniverseServer().getServer().send(new RegisterEntityPacket(entity), connection -> getInitiatedConnections().contains(connection));
        }
    }

    @Override
    public void unregister(Entity entity) {
        super.unregister(entity);
        if (entity instanceof Obstacle) {
            getUniverseServer().getCollisions().remove((Obstacle) entity);
        }
        if (entity.getNetworkExpectedSide() != NetworkSide.SERVER) {
            getUniverseServer().getServer().send(new UnregisterEntityPacket(entity.getUuid()), connection -> getInitiatedConnections().contains(connection));
        }
    }

    @Override
    public void finish() {
        super.finish();
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    public void update() {
        getUuidToEntity().values().forEach(Entity::serverUpdate);
        if (getNetworkTimer().isOver()) {
            EntitiesChanges entitiesChanges = findEntitiesChanges();
            if (!entitiesChanges.isEmpty()) {
                getUniverseServer().getServer().send(new EntitiesChangedClientPacket(entitiesChanges), connection -> getInitiatedConnections().contains(connection));
            }
            getNetworkTimer().drop();
        }
    }

    public void setCameraTarget(Entity entity) {
        this.cameraTarget = entity;
        getUniverseServer().getServer().send(new CameraTargetPacket(entity.getUuid()), connection -> getInitiatedConnections().contains(connection));
    }

    private void onClientConnect(OnClientConnect onClientConnect) {
        getUniverseServer().getServer().send(new InitiateConnectionPacket(getUuidToEntity().values().stream().filter(e -> e.getNetworkExpectedSide() != NetworkSide.SERVER).collect(Collectors.toSet())), connection -> connection == onClientConnect.getConnection());
        getUniverseServer().getServer().send(new CameraTargetPacket(getCameraTarget().getUuid()), connection -> connection == onClientConnect.getConnection());
        getInitiatedConnections().add(onClientConnect.getConnection());
    }

    private void onClientDisconnect(OnClientDisconnect onClientConnect) {
        getInitiatedConnections().remove(onClientConnect.getConnection());
    }

}
