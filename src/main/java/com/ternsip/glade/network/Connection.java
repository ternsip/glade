package com.ternsip.glade.network;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

@Getter
public class Connection {

    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    @SneakyThrows
    public Connection(Socket socket) {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    @SneakyThrows
    public void close() {
        getSocket().close();
    }

}
