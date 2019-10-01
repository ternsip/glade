package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.ui.UIInventory;
import com.ternsip.glade.universe.parts.items.Inventory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class UpdatePlayerInventoryClientPacket extends ClientPacket {

    private final UUID uuid;
    private final Inventory inventory;

    @Override
    public void apply(Connection connection) {
        EntityPlayer entityPlayer = (EntityPlayer) getUniverseClient().getEntityClientRepository().getEntityByUUID(getUuid());
        entityPlayer.setSelectionInventory(getInventory());
        getUniverseClient().getEntityClientRepository().getEntityByClass(UIInventory.class).updateSelectionInventory(getInventory());
    }

}
