package com.ternsip.glade.network;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
@Getter
@Setter
public class NetworkClient {

    private final Connection connection;

    private boolean active = true;

    @SneakyThrows
    public NetworkClient(String host, int port) {
        this.connection = new Connection(new Socket(host, port));
    }

    public void loop() {
        while (isActive()) {
            acceptData();
        }
    }

    @SneakyThrows
    public void finish() {
        setActive(false);
        getConnection().close();
    }

    private void acceptData() {
        try {
            Object obj = getConnection().readObject();
        } catch (Exception e) {
            if (isActive()) {
                String errMsg = String.format("Error while accepting data from server %s", e.getMessage());
                log.error(errMsg);
                log.debug(errMsg, e);
            }
        }
    }

}
