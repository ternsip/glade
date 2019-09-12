package com.ternsip.glade.network;

import com.ternsip.glade.common.events.network.OnClientConnect;
import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.common.logic.Threadable;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Getter
@Setter
public class NetworkServer implements Threadable, INetworkServerEventReceiver {

    private final long RETRY_INTERVAL = 500L;

    private final ConcurrentLinkedQueue<Connection> connections = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Packet> packets = new ConcurrentLinkedQueue<>();

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
                        Packet packet = connection.readPacket();
                        packet.apply(connection);
                    }
                } catch (Exception e) {
                    if (connection.isActive()) {
                        String errMsg = String.format("Error while accepting data from client %s", e.getMessage());
                        log.error(errMsg, e); // TODO do not write stack trace, its only for testing purposes (and use debug mod)
                        log.debug(errMsg, e);
                    }
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
        getConnections().forEach(Connection::close);
        getServerHolder().close();
    }

    public void sendAll(Packet packet) {
        getPackets().add(packet);
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

    public class Acceptor implements Threadable {

        @Override
        public void init() {}

        @Override
        public void update() {
            if (getServerHolder().isActive()) {
                try {
                    Connection connection = getServerHolder().accept();
                    getConnections().add(connection);
                    getNetworkServerEventReceiver().registerEvent(OnClientConnect.class, new OnClientConnect(connection));
                } catch (Exception e) {
                    if (getServerHolder().isActive()) {
                        String errMsg = String.format("Error while accepting new connection to server %s", e.getMessage());
                        log.error(errMsg, e); // TODO do not write stack trace, its only for testing purposes (and use debug mod)
                        log.debug(errMsg, e);
                    }
                }
                getConnections().removeIf(connection -> !connection.isActive());
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
            while (getServerHolder().isActive() && !getPackets().isEmpty()) {
                Packet packet = getPackets().poll();
                try {
                    getConnections().forEach(connection -> connection.writePacket(packet));
                } catch (Exception e) {
                    String errMsg = String.format("Error while sending packet from server %s", e.getMessage());
                    log.error(errMsg);
                    log.debug(errMsg, e);
                }
            }
        }

        @Override
        public void finish() {}

    }


}
