package com.ternsip.glade.network;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.common.logic.TimeNormalizer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@Getter
@Setter
public class NetworkServer implements Threadable {

    private final long RETRY_INTERVAL = 500L;

    private final ArrayList<Connection> connections = new ArrayList<>();
    private final TimeNormalizer timeNormalizer = new TimeNormalizer(1000L / 128);

    private ThreadWrapper<Acceptor> acceptorThread;
    private ServerHolder serverHolder = new ServerHolder();

    @SneakyThrows
    public void bind(int port) {
        setServerHolder(new ServerHolder(port));
    }

    @Override
    public void init() {
        acceptorThread = new ThreadWrapper<>(Acceptor::new);
    }

    public void stop() {
        getAcceptorThread().stop();
        getConnections().forEach(Connection::close);
        getServerHolder().close();
    }

    @Override
    public void update() {
        if (getServerHolder().isActive()) {
            getTimeNormalizer().drop();
            getConnections().forEach(connection -> {
                try {
                    if (connection.getInput().available() > 0) {
                        Packet packet = (Packet)connection.readObject();
                        packet.apply(connection);
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

    public void sendAll(Packet packet) {
        getConnections().forEach(connection -> connection.writeObject(packet));
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

    @Override
    public void finish() {}

    public class Acceptor implements Threadable {

        @Override
        public void init() {}

        @Override
        public void update() {
            if (getServerHolder().isActive()) {
                try {
                    getConnections().add(getServerHolder().accept());
                } catch (Exception e) {
                    if (getServerHolder().isActive()) {
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
