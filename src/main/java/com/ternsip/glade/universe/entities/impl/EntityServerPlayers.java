package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.events.network.OnClientDisconnect;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.EffigyDummy;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
@ServerSide
public class EntityServerPlayers extends Entity {

    private Callback<OnClientConnect> onClientConnectCallback = this::onClientConnect;
    private Callback<OnClientDisconnect> onClientDisconnectCallback = this::onClientDisconnect;
    private Map<Connection, PlayerSession> connectionToPlayerSession = new HashMap<>();

    @Override
    public void onServerRegister() {
        super.onServerRegister();
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    @Override
    public void onServerUnregister() {
        super.onServerUnregister();
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    @Override
    public Effigy getEffigy() {
        return new EffigyDummy();
    }

    @Override
    public void serverUpdate() {
        super.serverUpdate();
    }

    private void onClientConnect(OnClientConnect onClientConnect) {
        EntityPlayer entityPlayer = new EntityPlayer();
        entityPlayer.setPosition(new Vector3f(50, 90, 50));
        entityPlayer.setScale(new Vector3f(1, 1, 1));
        entityPlayer.register();

        EntityCubeSelection entityCubeSelection = new EntityCubeSelection(entityPlayer);
        entityCubeSelection.register();
        PlayerSession playerSession = new PlayerSession(entityPlayer, entityCubeSelection);
        getConnectionToPlayerSession().put(onClientConnect.getConnection(), playerSession);
    }

    private void onClientDisconnect(OnClientDisconnect onClientDisconnect) {
        getConnectionToPlayerSession().remove(onClientDisconnect.getConnection());
    }

    @RequiredArgsConstructor
    @Getter
    public static class PlayerSession {

        private final EntityPlayer entityPlayer;
        private final EntityCubeSelection entityCubeSelection;

    }

}
