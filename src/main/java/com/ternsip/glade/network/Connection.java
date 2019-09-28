package com.ternsip.glade.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@RequiredArgsConstructor
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

    public boolean isActive() {
        return getSocket() != null && !getSocket().isClosed();
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return getInput().readObject();
    }

    public void writeObject(Object object) throws IOException {
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


    public boolean isAvailable() throws IOException {
        return getSocket().getInputStream().available() > 0;
    }
}
