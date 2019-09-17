package com.ternsip.glade.network;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@Getter
public class Connection {

    private final Socket socket;

    @Getter(lazy = true)
    private final ObjectInputStream input = getInputBlocking();

    @Getter(lazy = true)
    private final ObjectOutputStream output = getOutputBlocking();

    public Connection() {
        this.socket = null;
    }

    @SneakyThrows
    public Connection(Socket socket) {
        this.socket = socket;
    }

    public boolean isActive() {
        return getSocket() != null && !getSocket().isClosed();
    }

    @SneakyThrows
    public Object readObject() {
        return getInput().readObject();
    }

    @SneakyThrows
    public void writeObject(Object object) {
        getOutput().writeObject(object);
    }

    @SneakyThrows
    public void close() {
        getSocket().shutdownInput();
        getSocket().shutdownOutput();
        getSocket().close();
    }

    @SneakyThrows
    public ObjectInputStream getInputBlocking() {
        return new ObjectInputStream(getSocket().getInputStream());
    }

    @SneakyThrows
    public ObjectOutputStream getOutputBlocking() {
        return new ObjectOutputStream(getSocket().getOutputStream());
    }

    @SneakyThrows
    public boolean isAvailable() {
        return getSocket().getInputStream().available() > 0;
    }
}
