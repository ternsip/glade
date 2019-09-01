package com.ternsip.glade.network;

import com.ternsip.glade.common.logic.Threadable;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
@Getter
@Setter
public class NetworkClient implements Threadable {

    private final long RETRY_INTERVAL = 500L;
    private final int MAX_CONNECTION_ATTEMPTS = 10;
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
    public void init() {}

    @Override
    public void update() {
        if (getConnection().isActive()) {
            try {
                ServerPacket packet = (ServerPacket) getConnection().readObject();
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

    public void send(ServerPacket packet) {
        getConnection().writeObject(packet);
    }

    public void stop() {
        getConnection().close();
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

}
