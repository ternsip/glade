package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.common.events.network.OnConnectedToServer;
import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;


@RequiredArgsConstructor
@Getter
@Slf4j
public class InitiateConnectionPacket extends ClientPacket {

    private final Collection<RegisterEntityPacket> entitiesToRegisterPackets;

    @Override
    public void apply(Connection connection) {
        getEntitiesToRegisterPackets().forEach(e -> e.apply(connection));
        getUniverseClient().getNetworkClientEventReceiver().registerEvent(OnConnectedToServer.class, new OnConnectedToServer(connection));
    }

}
