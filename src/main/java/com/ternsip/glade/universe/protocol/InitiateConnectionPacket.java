package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.common.events.network.OnConnectedToServer;
import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.stream.Collectors;


@Getter
@Slf4j
public class InitiateConnectionPacket extends ClientPacket {

    private final Collection<RegisterEntityPacket> entitiesToRegisterPackets;

    public InitiateConnectionPacket(Collection<Entity> entities) {
        this.entitiesToRegisterPackets = entities.stream().map(RegisterEntityPacket::new).collect(Collectors.toList());
    }

    @Override
    public void apply(Connection connection) {
        getEntitiesToRegisterPackets().forEach(e -> e.apply(connection));
        getUniverseClient().getNetworkClientEventReceiver().registerEvent(OnConnectedToServer.class, new OnConnectedToServer(connection, true));
    }

}
