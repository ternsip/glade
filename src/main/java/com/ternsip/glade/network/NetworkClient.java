package com.ternsip.glade.network;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.common.logic.Threadable;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Getter
@Setter
public class NetworkClient implements Threadable {

    private final long RETRY_INTERVAL = 500L;
    private final int MAX_CONNECTION_ATTEMPTS = 10;
    private final ConcurrentLinkedQueue<Packet> packets = new ConcurrentLinkedQueue<>();

    private ThreadWrapper<Sender> senderThread;
    private Connection connection = new Connection();

    public void connect(String host, int port) {
        for (int attempt = 0; attempt < MAX_CONNECTION_ATTEMPTS; ++attempt) {
            try {
                setConnection(new Connection(new Socket(host, port)));
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
        setSenderThread(new ThreadWrapper<>(Sender::new, 150));
    }

    @Override
    public void update() {
        if (getConnection().isActive()) {
            try {
                Packet packet = (Packet) getConnection().readObject();
                packet.apply(getConnection());
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

    public void send(Packet packet) {
        getPackets().add(packet);
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
                Packet packet = getPackets().poll();
                try {
                    getConnection().writeObject(packet);
                } catch (Exception e) {
                    String errMsg = String.format("Error while sending packet %s", e.getMessage());
                    log.error(errMsg);
                    log.debug(errMsg, e);
                }
            }
        }

        @Override
        public void finish() {}

    }

}
