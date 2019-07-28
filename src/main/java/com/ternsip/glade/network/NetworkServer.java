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
    private final ArrayList<Connection> connections = new ArrayList<>();

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
        getConnections().forEach(Connection::close);
        getServerSocket().close();
    }

    public void sendAll(Object obj) {
        getConnections().forEach(connection -> connection.writeObject(obj));
    }

    private void acceptNewConnection() {
        try {
            Connection connection = new Connection(getServerSocket().accept());
            getConnections().add(connection);
        } catch (Exception e) {
            if (isActive()) {
                String errMsg = String.format("Error while accepting new connection to server %s", e.getMessage());
                log.error(errMsg);
                log.debug(errMsg, e);
            }
        }
    }

}
