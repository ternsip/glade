package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.parts.items.Item;
import com.ternsip.glade.universe.parts.items.ItemEmpty;
import com.ternsip.glade.universe.protocol.UpdatePlayerInventoryClientPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UseItemAction extends BaseAction {

    private final int slot;

    @Override
    public void apply(EntityPlayerServer player) {
        Item item = player.getSelectionInventory().getItems()[getSlot()];
        item.use(player);
        if (item.getCount() <= 0) {
            player.getSelectionInventory().getItems()[getSlot()] = new ItemEmpty();
        }
        getUniverseServer().getServer().send(new UpdatePlayerInventoryClientPacket(player.getUuid(), player.getSelectionInventory()), player.getAllowedConnection());
    }
    
}
