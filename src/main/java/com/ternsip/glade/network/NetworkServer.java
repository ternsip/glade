package com.ternsip.glade.network;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.util.ArrayList;

@Slf4j
@Getter
@Setter
public class NetworkServer {

    private final ServerSocket serverSocket;
    private final ArrayList<ServerConnection> connections = new ArrayList<>();

    private boolean active = true;

    @SneakyThrows
    public NetworkServer(int port) {
        this.serverSocket = new ServerSocket(port);
    }

    public void loop() {
        while (isActive()) {
            acceptNewConnection();
        }
    }

    @SneakyThrows
    public void finish() {
        setActive(false);
        getServerSocket().close();
    }

    private void acceptNewConnection() {
        try {
            ServerConnection serverConnection = new ServerConnection(getServerSocket().accept());
            getConnections().add(serverConnection);
        } catch (Exception e) {
            if (isActive()) {
                String errMsg = String.format("Error while accepting new connection to server %s", e.getMessage());
                log.error(errMsg);
                log.debug(errMsg, e);
            }
        }
    }

}
