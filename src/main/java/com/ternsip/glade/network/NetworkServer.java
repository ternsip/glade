package com.ternsip.glade.network;

import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.events.network.OnClientDisconnect;
import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

@Slf4j
@Getter
@Setter
public class NetworkServer implements Threadable, IUniverseServer {

    private final long RETRY_INTERVAL = 500L;

    private final Set<Connection> connections = ConcurrentHashMap.newKeySet();
    private final ConcurrentLinkedQueue<PacketToSend> packetsToSend = new ConcurrentLinkedQueue<>();

    private ServerHolder serverHolder = new ServerHolder();
    private ThreadWrapper<Acceptor> acceptorThread;
    private ThreadWrapper<Sender> senderThread;

    @SneakyThrows
    public void bind(int port) {
        setServerHolder(new ServerHolder(port));
    }

    @Override
    public void init() {
        setAcceptorThread(new ThreadWrapper<>(Acceptor::new));
        setSenderThread(new ThreadWrapper<>(Sender::new));
    }

    @Override
    public void update() {
        if (getServerHolder().isActive()) {
            getConnections().forEach(connection -> {
                try {
                    if (connection.getInput().available() > 0) {
                        ServerPacket serverPacket = (ServerPacket) connection.readObject();
                        serverPacket.apply(connection);
                    }
                } catch (Exception e) {
                    disconnectClient(connection, e);
                }
            });
        } else {
            snooze();
        }
    }

    @Override
    public void finish() {}

    public void stop() {
        getAcceptorThread().stop();
        getSenderThread().stop();
        getConnections().forEach(this::disconnectClient);
        if (getServerHolder().isActive()) {
            getServerHolder().close();
        }
    }

    public void send(ClientPacket packet, Function<Connection, Boolean> connectionCondition) {
        getPacketsToSend().add(new PacketToSend(packet, connectionCondition));
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

    private void addConnection(Connection connection) {
        getConnections().add(connection);
        getUniverseServer().getNetworkServerEventReceiver().registerEvent(OnClientConnect.class, new OnClientConnect(connection));
    }

    private void disconnectClient(Connection connection) {
        if (connection.isActive()) {
            connection.close();
            getUniverseServer().getNetworkServerEventReceiver().registerEvent(OnClientDisconnect.class, new OnClientDisconnect(connection));
            getConnections().remove(connection);
        }
    }

    private void disconnectClient(Connection connection, Exception e) {
        if (!connection.isActive()) {
            throw new IllegalArgumentException("Connection is not active. This situation should not happen.");
        }
        String errMsg = String.format("Client disconnected from server %s - %s", e.getClass().getSimpleName(), e.getMessage());
        log.error(errMsg);
        log.debug(errMsg, e);
        disconnectClient(connection);
    }

    public class Acceptor implements Threadable {

        @Override
        public void init() {}

        @Override
        public void update() {
            if (getServerHolder().isActive()) {
                try {
                    addConnection(getServerHolder().accept());
                } catch (Exception e) {
                    if (getServerHolder().isActive()) {
                        String errMsg = String.format("Error while accepting new connection to server %s", e.getMessage());
                        log.error(errMsg);
                        log.debug(errMsg, e);
                    }
                }
            }
        }

        @Override
        public void finish() {}

    }

    public class Sender implements Threadable {

        @Override
        public void init() {}

        @Override
        public void update() {
            while (getServerHolder().isActive() && !getPacketsToSend().isEmpty()) {
                PacketToSend packetToSend = getPacketsToSend().poll();
                if (packetToSend == null) {
                    continue;
                }
                getConnections().forEach(connection -> sendPacket(packetToSend, connection));
            }
        }

        @Override
        public void finish() {}

        private void sendPacket(PacketToSend packetToSend, Connection connection) {
            if (!packetToSend.getConnectionCondition().apply(connection)) {
                return;
            }
            try {
                connection.writeObject(packetToSend.getClientPacket());
            } catch (Exception e) {
                disconnectClient(connection, e);
            }
        }

    }

    @RequiredArgsConstructor
    @Getter
    private static class PacketToSend {

        private final ClientPacket clientPacket;
        private final Function<Connection, Boolean> connectionCondition;

    }


}
