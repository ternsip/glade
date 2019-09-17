package com.ternsip.glade.network;

import com.ternsip.glade.common.events.network.OnConnectToServer;
import com.ternsip.glade.common.events.network.OnDisconnectedFromServer;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
@Getter
@Setter
public class NetworkClient implements Threadable, IUniverseClient {

    private final long RETRY_INTERVAL = 500L;
    private final int MAX_CONNECTION_ATTEMPTS = 10;

    private Connection connection = new Connection();

    public void connect(String host, int port) {
        for (int attempt = 0; attempt < MAX_CONNECTION_ATTEMPTS; ++attempt) {
            try {
                establishConnection(new Connection(new Socket(host, port)));
                return;
            } catch (Exception e) {
                String errMsg = String.format("Unable to connect to %s:%s, Attempt: #%s, Reason: %s retrying...", host, port, attempt, e.getMessage());
                log.error(errMsg);
                log.debug(errMsg, e);
                snooze();
            }
        }
        throw new IllegalArgumentException("Give up trying to connect!");
    }

    @Override
    public void init() {
    }

    @Override
    public void update() {
        if (getConnection().isActive()) {
            try {
                ClientPacket clientPacket = (ClientPacket) getConnection().readObject();
                clientPacket.apply(getConnection());
            } catch (Exception e) {
                disconnect(e);
            }
        } else {
            snooze();
        }
    }

    @Override
    public void finish() {}

    public synchronized void send(ServerPacket serverPacket) {
        try {
            getConnection().writeObject(serverPacket);
        } catch (Exception e) {
            disconnect(e);
        }
    }

    public void stop() {
        disconnect();
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

    private void establishConnection(Connection connection) {
        setConnection(connection);
        getUniverseClient().getNetworkClientEventReceiver().registerEvent(OnConnectToServer.class, new OnConnectToServer(connection));
    }

    private void disconnect() {
        if (getConnection().isActive()) {
            getConnection().close();
            getUniverseClient().getNetworkClientEventReceiver().registerEvent(OnDisconnectedFromServer.class, new OnDisconnectedFromServer());
        }
    }

    private void disconnect(Exception e) {
        if (!getConnection().isActive()) {
            throw new IllegalArgumentException("Connection is not active. This situation should not happen.");
        }
        String errMsg = String.format("Disconnected from server %s - %s", e.getClass().getSimpleName(), e.getMessage());
        log.error(errMsg);
        log.debug(errMsg, e);
        disconnect();
    }

}
