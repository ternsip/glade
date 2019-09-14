package com.ternsip.glade.network;

import com.ternsip.glade.common.events.network.OnConnectedToServer;
import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Getter
@Setter
public class NetworkClient implements Threadable, IUniverseClient {

    private final long RETRY_INTERVAL = 500L;
    private final int MAX_CONNECTION_ATTEMPTS = 10;
    private final ConcurrentLinkedQueue<ServerPacket> packets = new ConcurrentLinkedQueue<>();

    private ThreadWrapper<Sender> senderThread;
    private Connection connection = new Connection();

    public void connect(String host, int port) {
        for (int attempt = 0; attempt < MAX_CONNECTION_ATTEMPTS; ++attempt) {
            try {
                Connection connection = new Connection(new Socket(host, port));
                setConnection(connection);
                getUniverseClient().getNetworkClientEventReceiver().registerEvent(OnConnectedToServer.class, new OnConnectedToServer(connection, false));
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
        setSenderThread(new ThreadWrapper<>(Sender::new));
    }

    @Override
    public void update() {
        if (getConnection().isActive()) {
            try {
                ClientPacket clientPacket = (ClientPacket) getConnection().readObject();
                clientPacket.apply(getConnection());
            } catch (Exception e) {
                if (getConnection().isActive()) {
                    String errMsg = String.format("Error while accepting data from server %s", e.getMessage());
                    log.error(errMsg);
                    log.debug(errMsg, e);
                }
            }

        } else {
            snooze();
        }
    }

    @Override
    public void finish() {}

    public void send(ServerPacket serverPacket) {
        getPackets().add(serverPacket);
    }

    public void stop() {
        getSenderThread().stop();
        getConnection().close();
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

    public class Sender implements Threadable {

        @Override
        public void init() {}

        @Override
        public void update() {
            while (getConnection().isActive() && !getPackets().isEmpty()) {
                ServerPacket serverPacket = getPackets().poll();
                try {
                    getConnection().writeObject(serverPacket);
                } catch (Exception e) {
                    String errMsg = String.format("Error while sending packet from client %s", e.getMessage());
                    log.error(errMsg);
                    log.debug(errMsg, e);
                }
            }
        }

        @Override
        public void finish() {}

    }

}
