package com.ternsip.glade.network;

import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class ClientPacket implements Serializable, IUniverseClient {

    public abstract void apply(Connection connection);

}
