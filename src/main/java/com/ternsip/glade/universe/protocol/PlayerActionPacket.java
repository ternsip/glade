package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerActionPacket extends ServerPacket {

    private final UUID entityUuid;
    private final EntityPlayer.Action action;

    public PlayerActionPacket(Entity entity, EntityPlayer.Action action) {
        this.entityUuid = entity.getUuid();
        this.action = action;
    }

    @Override
    public void apply(Connection connection) {
        EntityPlayer entityPlayer = (EntityPlayer) getUniverseServer().getEntityServerRepository().getUuidToEntity().get(getEntityUuid());
        entityPlayer.handleAction(getAction());
    }

}
