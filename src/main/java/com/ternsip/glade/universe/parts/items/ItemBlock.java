package com.ternsip.glade.universe.parts.items;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
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
    public void useOnServer(EntityPlayerServer player) {
        Vector3ic pos = getUniverseServer().getBlocksServerRepository().traverse(player.getEyeSegment(), (b, p) -> b.isObstacle());
        if (pos != null && getUniverseServer().getBlocksServerRepository().isBlockExists(pos)) {
            Vector3ic prevPos = getUniverseServer().getBlocksServerRepository().traverse(player.getEyeSegment(), (b, p) -> p.distanceSquared(pos) == 1);
            if (prevPos != null) {
                getUniverseServer().getBlocksServerRepository().setBlock(prevPos, getBlock());
                setCount(getCount() - 1);
            }
        }
    }

    @Override
    public void useOnClient(EntityPlayer player) {
        Vector3ic pos = getUniverseClient().getBlocksClientRepository().traverse(player.getEyeSegment(), (b, p) -> b.isObstacle());
        if (pos != null && getUniverseClient().getBlocksClientRepository().isBlockExists(pos)) {
            Vector3ic prevPos = getUniverseClient().getBlocksClientRepository().traverse(player.getEyeSegment(), (b, p) -> p.distanceSquared(pos) == 1);
            if (prevPos != null) {
                getUniverseClient().getBlocksClientRepository().setBlock(prevPos, getBlock());
                setCount(getCount() - 1);
            }
        }
    }

    @Override
    public Object getKey() {
        return getBlock();
    }

}
