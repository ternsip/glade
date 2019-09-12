package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.Packet;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;

@Getter
public abstract class ClientPacket extends Packet implements IUniverseClient {

    public abstract void apply(Connection connection);

}
