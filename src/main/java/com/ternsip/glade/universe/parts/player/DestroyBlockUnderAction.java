package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class DestroyBlockUnderAction extends BaseAction {

    @Override
    public void applyOnServer(EntityPlayerServer player) {
        Vector3ic blockUnder = new Vector3i(
                (int) Math.floor(player.getPosition().x()),
                (int) Math.floor(player.getPosition().y()) - 1,
                (int) Math.floor(player.getPosition().z())
        );
        if (getUniverseServer().getBlocksServerRepository().isBlockExists(blockUnder)) {
            getUniverseServer().getBlocksServerRepository().setBlock(blockUnder, Block.AIR);
        }
    }

    @Override
    public void applyOnClient(EntityPlayer player) {
        Vector3ic blockUnder = new Vector3i(
                (int) Math.floor(player.getPosition().x()),
                (int) Math.floor(player.getPosition().y()) - 1,
                (int) Math.floor(player.getPosition().z())
        );
        if (getUniverseClient().getBlocksClientRepository().isBlockExists(blockUnder)) {
            getUniverseClient().getBlocksClientRepository().setBlock(blockUnder, Block.AIR);
        }
    }

}
