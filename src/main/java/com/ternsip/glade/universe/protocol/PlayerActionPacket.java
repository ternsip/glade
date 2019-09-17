package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerPacket;
import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerActionPacket extends ServerPacket {

    private final UUID entityUuid;
    private final EntityPlayer.Action action;

    public PlayerActionPacket(EntityPlayer entityPlayer, EntityPlayer.Action action) {
        this.entityUuid = entityPlayer.getUuid();
        this.action = action;
    }

    @Override
    public void apply(Connection connection) {
        EntityPlayer entityPlayer = (EntityPlayer) getUniverseServer().getEntityServerRepository().getEntityByUUID(getEntityUuid());
        entityPlayer.handleAction(getAction());
    }

}
