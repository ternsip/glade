package com.ternsip.glade.network;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

@Getter
public class ServerConnection {

    private final Socket socket;
    private final DataInputStream inFromClient;
    private final DataOutputStream outToClient;

    @SneakyThrows
    public ServerConnection(Socket socket) {
        this.socket = socket;
        this.inFromClient = new DataInputStream(socket.getInputStream());
        this.outToClient = new DataOutputStream(socket.getOutputStream());
    }
}
