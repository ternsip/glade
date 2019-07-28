package com.ternsip.glade.network;

import com.ternsip.glade.common.logic.TimeNormalizer;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.util.ArrayList;

@Slf4j
@Getter
@Setter
public class NetworkServer extends NetworkHandler implements Universal {

    private final ServerSocket serverSocket;
    private final ArrayList<Connection> connections = new ArrayList<>();
    private final TimeNormalizer timeNormalizer = new TimeNormalizer((long) (1000.0f / getUniverse().getBalance().getTicksPerSecond()));

    @SneakyThrows
    public NetworkServer(int port) {
        this.serverSocket = new ServerSocket(port);
    }

    public void loop() {
        new Thread(this::processConnections).start();
        while (!getServerSocket().isClosed()) {
            acceptNewConnection();
        }
    }

    @SneakyThrows
    public void processConnections() {
        while (!getServerSocket().isClosed()) {
            getTimeNormalizer().drop();
            handleInputMessages();
            getTimeNormalizer().rest();
        }
    }

    @SneakyThrows
    public void finish() {
        getConnections().forEach(Connection::close);
        getServerSocket().close();
    }

    public void sendAll(Object obj) {
        getConnections().forEach(connection -> connection.writeObject(obj));
    }

    private void handleInputMessages() {
        getConnections().forEach(connection -> {
            try {
                if (connection.getInput().available() > 0) {
                    handleObject(connection, connection.readObject());
                }
            } catch (Exception e) {
                if (!connection.getSocket().isClosed()) {
                    String errMsg = String.format("Error while accepting data from server %s", e.getMessage());
                    log.error(errMsg);
                    log.debug(errMsg, e);
                }
            }
        });
    }

    private void acceptNewConnection() {
        try {
            Connection connection = new Connection(getServerSocket().accept());
            getConnections().add(connection);
        } catch (Exception e) {
            if (!getServerSocket().isClosed()) {
                String errMsg = String.format("Error while accepting new connection to server %s", e.getMessage());
                log.error(errMsg);
                log.debug(errMsg, e);
            }
        }
    }

}
