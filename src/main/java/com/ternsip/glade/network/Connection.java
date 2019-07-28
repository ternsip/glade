package com.ternsip.glade.network;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

@Getter
public class Connection {

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    @SneakyThrows
    public Connection(Socket socket) {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
    }

    @SneakyThrows
    public Object readObject() {
        return new ObjectInputStream(getInput()).readObject();
    }

    @SneakyThrows
    public void writeObject(Object object) {
        new ObjectOutputStream(getOutput()).writeObject(object);
    }

    @SneakyThrows
    public void close() {
        getSocket().shutdownInput();
        getSocket().shutdownOutput();
        getSocket().close();
    }

}
