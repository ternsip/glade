package com.ternsip.glade.universe.parts.items;

import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
@Setter
public class ItemBlock extends Item {

    private final Block block;

    @Override
    public void use(EntityPlayerServer player) {
        Vector3ic pos = player.getUniverseServer().getBlocksRepository().traverse(player.getEyeSegment(), (b, p) -> b.isObstacle());
        if (pos != null && player.getUniverseServer().getBlocksRepository().isBlockExists(pos)) {
            Vector3ic prevPos = player.getUniverseServer().getBlocksRepository().traverse(player.getEyeSegment(), (b, p) -> p.distanceSquared(pos) == 1);
            if (prevPos != null) {
                player.getUniverseServer().getBlocksRepository().setBlock(prevPos, getBlock());
            }
        }
    }

    @Override
    public Object getKey() {
        return getBlock();
    }

}
