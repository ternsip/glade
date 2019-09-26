package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.common.events.network.OnConnectedToServer;
import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.EntityServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Getter
@Slf4j
public class InitiateConnectionClientPacket extends ClientPacket {

    private final ArrayList<RegisterEntityClientPacket> entitiesToRegisterPackets;

    public InitiateConnectionClientPacket(Collection<EntityServer> serverEntities, Connection connection) {
        this.entitiesToRegisterPackets = serverEntities.stream()
                .filter(EntityServer::isTransferable)
                .map(e -> new RegisterEntityClientPacket(e, connection))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void apply(Connection connection) {
        getEntitiesToRegisterPackets().forEach(e -> e.apply(connection));
        getUniverseClient().getNetworkClientEventReceiver().registerEvent(OnConnectedToServer.class, new OnConnectedToServer(connection));
    }

}
