package com.ternsip.glade.network;

import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.events.network.OnClientDisconnect;
import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Getter
@Setter
public class NetworkServer implements Threadable, IUniverseServer {

    private final long RETRY_INTERVAL = 500L;

    private final Set<Connection> connections = ConcurrentHashMap.newKeySet();

    private ServerHolder serverHolder = new ServerHolder();
    private LazyThreadWrapper<Acceptor> acceptorThread = new LazyThreadWrapper<>(Acceptor::new);

    @SneakyThrows
    public void bind(int port) {
        setServerHolder(new ServerHolder(port));
    }

    @Override
    public void init() {
        getAcceptorThread().touch();
    }

    @Override
    public void update() {
        if (getServerHolder().isActive()) {
            removeInactiveConnections();
            getConnections().forEach(this::receive);
        } else {
            snooze();
        }
    }

    @Override
    public void finish() {}

    public void stop() {
        getAcceptorThread().getThreadWrapper().stop();
        getConnections().forEach(this::disconnectClient);
        if (getServerHolder().isActive()) {
            getServerHolder().close();
        }
    }

    public synchronized void send(ClientPacket clientPacket, Function<Connection, Boolean> connectionCondition) {
        getConnections().forEach(connection -> {
            if (connectionCondition.apply(connection)) {
                send(clientPacket, connection);
            }
        });
    }

    public synchronized void send(ClientPacket clientPacket, Connection connection) {
        try {
            connection.writeObject(clientPacket);
        } catch (SocketException | EOFException e) {
            handleClientTermination(connection, e);
        } catch (Exception e) {
            String errMsg = String.format("Error while sending packet %s packet to client (%s) - %s", e.getClass().getSimpleName(), connection, e.getMessage());
            log.error(errMsg);
            log.debug(errMsg, e);
        }
    }

    private void removeInactiveConnections() {
        getConnections().removeIf(connection -> {
            if (!connection.isActive()) {
                getUniverseServer().getNetworkServerEventReceiver().registerEvent(OnClientDisconnect.class, new OnClientDisconnect(connection));
                return true;
            }
            return false;
        });
    }

    private void receive(Connection connection) {
        try {
            if (connection.isAvailable()) {
                ServerPacket serverPacket = (ServerPacket) connection.readObject();
                serverPacket.apply(connection);
            }
        } catch (SocketException | EOFException e) {
            handleClientTermination(connection, e);
        } catch (Exception e) {
            String errMsg = String.format("Can not apply %s packet from client (%s) - %s", e.getClass().getSimpleName(), connection, e.getMessage());
            log.error(errMsg);
            log.debug(errMsg, e);
        }
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
            log.info(String.format("Client %s has been disconnected", connection));
        } else {
            throw new IllegalArgumentException(String.format("Can not disconnect client because connection %s is not active", connection));
        }
    }

    private void handleClientTermination(Connection connection, Exception e) {
        disconnectClient(connection);
        log.debug("Connection to client has been terminated", e);
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

}
