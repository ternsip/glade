package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import org.joml.Vector3ic;

public class DestroySelectedBlockAction extends BaseAction {

    @Override
    public void apply(EntityPlayerServer player) {
        Vector3ic blockPositionLooking = getUniverseServer().getBlocksRepository().traverse(player.getEyeSegment(), (b, p) -> b != Block.AIR);
        if (blockPositionLooking != null && getUniverseServer().getBlocksRepository().isBlockExists(blockPositionLooking)) {
            getUniverseServer().getBlocksRepository().setBlock(blockPositionLooking, Block.AIR);
        }
    }

}
