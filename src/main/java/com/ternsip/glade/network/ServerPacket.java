package com.ternsip.glade.network;

import com.ternsip.glade.universe.interfaces.IUniverseServer;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class ServerPacket implements Serializable, IUniverseServer {

    public abstract void apply(Connection connection);

}
