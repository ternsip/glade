package com.ternsip.glade.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;

@RequiredArgsConstructor
@Slf4j
@Getter
public class ServerConnection {

    private final ServerSocket socket;

    public ServerConnection() {
        socket = null;
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
