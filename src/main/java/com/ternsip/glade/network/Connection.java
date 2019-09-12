package com.ternsip.glade.network;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
@Getter
public class Connection {

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public Connection() {
        socket = null;
        input = null;
        output = null;
    }

    @SneakyThrows
    public Connection(Socket socket) {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
    }

    public boolean isActive() {
        return getSocket() != null && !getSocket().isClosed();
    }

    @SneakyThrows
    public Packet readPacket() {
        return ((Packet) new ObjectInputStream(getInput()).readObject());
    }

    @SneakyThrows
    public void writePacket(Packet packet) {
        new ObjectOutputStream(getOutput()).writeObject(packet);
    }

    @SneakyThrows
    public void close() {
        getSocket().shutdownInput();
        getSocket().shutdownOutput();
        getSocket().close();
    }

}
