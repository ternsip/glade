package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import org.joml.Vector3ic;

public class DestroySelectedBlockAction extends BaseAction {

    @Override
    public void applyOnServer(EntityPlayerServer player) {
        Vector3ic blockPositionLooking = getUniverseServer().getBlocksServerRepository().traverse(player.getEyeSegment(), (b, p) -> b != Block.AIR);
        if (blockPositionLooking != null && getUniverseServer().getBlocksServerRepository().isBlockExists(blockPositionLooking)) {
            getUniverseServer().getBlocksServerRepository().setBlock(blockPositionLooking, Block.AIR);
        }
    }

    @Override
    public void applyOnClient(EntityPlayer player) {
        Vector3ic blockPositionLooking = getUniverseClient().getBlocksClientRepository().traverse(player.getEyeSegment(), (b, p) -> b != Block.AIR);
        if (blockPositionLooking != null && getUniverseClient().getBlocksClientRepository().isBlockExists(blockPositionLooking)) {
            getUniverseClient().getBlocksClientRepository().setBlock(blockPositionLooking, Block.AIR);
        }
    }

}
