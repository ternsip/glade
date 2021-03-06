package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.events.network.OnClientDisconnect;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.EntityServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
public class EntityPlayerListServer extends EntityServer {

    private Callback<OnClientConnect> onClientConnectCallback = this::onClientConnect;
    private Callback<OnClientDisconnect> onClientDisconnectCallback = this::onClientDisconnect;
    private Map<Connection, PlayerSession> connectionToPlayerSession = new HashMap<>();

    @Override
    public void register() {
        super.register();
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().registerCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    @Override
    public void unregister() {
        super.unregister();
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientConnect.class, getOnClientConnectCallback());
        getUniverseServer().getNetworkServerEventReceiver().unregisterCallback(OnClientDisconnect.class, getOnClientDisconnectCallback());
    }

    private void onClientConnect(OnClientConnect onClientConnect) {
        EntityPlayerServer entityPlayerServer = new EntityPlayerServer(onClientConnect.getConnection());
        entityPlayerServer.setPosition(new Vector3f(50, 190, 50));
        entityPlayerServer.setScale(new Vector3f(1, 1, 1));
        entityPlayerServer.register();

        EntityCubeSelectionServer entityCubeSelectionServer = new EntityCubeSelectionServer(entityPlayerServer);
        entityCubeSelectionServer.register();
        PlayerSession playerSession = new PlayerSession(entityPlayerServer, entityCubeSelectionServer);
        getConnectionToPlayerSession().put(onClientConnect.getConnection(), playerSession);
    }

    private void onClientDisconnect(OnClientDisconnect onClientDisconnect) {
        PlayerSession playerSession = getConnectionToPlayerSession().get(onClientDisconnect.getConnection());
        playerSession.getEntityPlayerServer().unregister();
        playerSession.getEntityCubeSelectionServer().unregister();
        getConnectionToPlayerSession().remove(onClientDisconnect.getConnection());
    }

    @RequiredArgsConstructor
    @Getter
    public static class PlayerSession {

        private final EntityPlayerServer entityPlayerServer;
        private final EntityCubeSelectionServer entityCubeSelectionServer;

    }

}
