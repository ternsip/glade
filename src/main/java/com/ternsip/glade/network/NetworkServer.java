package com.ternsip.glade.network;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.common.logic.TimeNormalizer;
import com.ternsip.glade.common.logic.Updatable;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.util.ArrayList;

@Slf4j
@Getter
@Setter
public class NetworkServer extends NetworkHandler implements Updatable {

    private final long RETRY_INTERVAL = 500L;

    private final ArrayList<Connection> connections = new ArrayList<>();
    private final TimeNormalizer timeNormalizer = new TimeNormalizer(1000L / 128);

    private ThreadWrapper<Acceptor> acceptorThread;
    private ServerConnection serverConnection = new ServerConnection();

    @SneakyThrows
    public void bind(int port) {
        setServerConnection(new ServerConnection(new ServerSocket(port)));
    }

    @Override
    public void init() {
        acceptorThread = new ThreadWrapper<>(new Acceptor());
    }

    @Override
    public void update() {
        if (getServerConnection().isActive()) {
            getTimeNormalizer().drop();
            getConnections().forEach(connection -> {
                try {
                    if (connection.getInput().available() > 0) {
                        handleObject(connection, connection.readObject());
                    }
                } catch (Exception e) {
                    if (connection.isActive()) {
                        String errMsg = String.format("Error while accepting data from server %s", e.getMessage());
                        log.error(errMsg);
                        log.debug(errMsg, e);
                    }
                }
            });
            getTimeNormalizer().rest();
        } else {
            snooze();
        }
    }

    public void stop() {
        getAcceptorThread().stop();
        getConnections().forEach(Connection::close);
        getServerConnection().close();
    }

    @Override
    public void finish() {}

    public void sendAll(Object obj) {
        getConnections().forEach(connection -> connection.writeObject(obj));
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

    public class Acceptor implements Updatable {

        @Override
        public void init() {}

        @Override
        public void update() {
            if (getServerConnection().isActive()) {
                try {
                    getConnections().add(getServerConnection().accept());
                } catch (Exception e) {
                    if (getServerConnection().isActive()) {
                        String errMsg = String.format("Error while accepting new connection to server %s", e.getMessage());
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

    }

}
