package com.ternsip.glade.network;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
@Getter
@Setter
public class NetworkClient extends NetworkHandler {

    private final long RETRY_INTERVAL = 500L;
    private final int MAX_CONNECTION_ATTEMPTS = 10;
    private final Connection connection;

    @SneakyThrows
    public NetworkClient(String host, int port) {
        Connection connection = null;
        for (int attempt = 0; attempt < MAX_CONNECTION_ATTEMPTS; ++attempt) {
            try {
                connection = new Connection(new Socket(host, port));
                break;
            } catch (Exception e) {
                String errMsg = String.format("Unable to connect to %s:%s, Attempt: #%s, Reason: %s retrying...", host, port, attempt, e.getMessage());
                log.error(errMsg);
                log.debug(errMsg, e);
                Thread.sleep(RETRY_INTERVAL);
            }
        }
        this.connection = connection;
    }

    public void loop() {
        while (!getConnection().getSocket().isClosed()) {
            try {
                handleObject(getConnection(), getConnection().readObject());
            } catch (Exception e) {
                if (!getConnection().getSocket().isClosed()) {
                    String errMsg = String.format("Error while accepting data from server %s", e.getMessage());
                    log.error(errMsg);
                    log.debug(errMsg, e);
                }
            }
        }
    }

    public void send(Object object) {
        getConnection().writeObject(object);
    }

    @SneakyThrows
    public void finish() {
        getConnection().close();
    }

}
