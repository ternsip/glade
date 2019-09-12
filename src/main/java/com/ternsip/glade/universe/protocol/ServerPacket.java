package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.Packet;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import lombok.Getter;

@Getter
// TODO move this on network level
public abstract class ServerPacket extends Packet implements IUniverseServer {

    public abstract void apply(Connection connection);

}
