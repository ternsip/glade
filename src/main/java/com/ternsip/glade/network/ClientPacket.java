package com.ternsip.glade.network;

import com.ternsip.glade.universe.interfaces.IUniverseClient;

import java.io.Serializable;

public interface ClientPacket extends Serializable, IUniverseClient {

    void apply(Connection connection);

}
