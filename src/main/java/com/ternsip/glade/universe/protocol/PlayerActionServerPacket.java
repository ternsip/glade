package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerPacket;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.parts.player.BaseAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class PlayerActionServerPacket extends ServerPacket {

    private final UUID entityUuid;
    private final BaseAction action;

    @Override
    public void apply(Connection connection) {
        getAction().applyOnServer((EntityPlayerServer) getUniverseServer().getEntityServerRepository().getEntityByUUID(getEntityUuid()));
    }

}
