package com.ternsip.glade.network;

import com.ternsip.glade.universe.interfaces.IUniverseServer;

import java.io.Serializable;

public interface ServerPacket extends Serializable, IUniverseServer {

    void apply(Connection connection);

}
