package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class DestroyBlockUnderAction extends BaseAction {

    @Override
    public void apply(EntityPlayerServer player) {
        Vector3ic blockUnder = new Vector3i(
                (int) Math.floor(player.getPosition().x()),
                (int) Math.floor(player.getPosition().y()) - 1,
                (int) Math.floor(player.getPosition().z())
        );
        if (getUniverseServer().getBlocksServerRepository().isBlockExists(blockUnder)) {
            getUniverseServer().getBlocksServerRepository().setBlock(blockUnder, Block.AIR);
        }
    }

}
