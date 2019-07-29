package com.ternsip.glade.network;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;

@Slf4j
@Getter
public class ServerHolder {

    private final ServerSocket socket;

    public ServerHolder() {
        socket = null;
    }

    @SneakyThrows
    public ServerHolder(int port) {
        socket = new ServerSocket(port);
    }

    public boolean isActive() {
        return getSocket() != null && !getSocket().isClosed();
    }

    @SneakyThrows
    public Connection accept() {
        return new Connection(getSocket().accept());
    }

    @SneakyThrows
    public void close() {
        getSocket().close();
    }

}
