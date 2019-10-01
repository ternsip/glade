package com.ternsip.glade.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
        return SerializationUtils.deserialize((byte[]) getInput().readObject());
    }

    public void writeObject(Serializable object) throws IOException {
        getOutput().writeObject(SerializationUtils.serialize(object));
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
