package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.impl.EntitySides;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import com.ternsip.glade.universe.parts.chunks.EngagedBlocks;
import com.ternsip.glade.universe.parts.chunks.SidePosition;
import com.ternsip.glade.universe.parts.chunks.Sides;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class BlockSidesUpdateClientPacket extends ClientPacket {

    private final Sides sidesToRemove = new Sides();
    private final Sides sidesToAdd = new Sides();
    private final EngagedBlocks blocksToChange = new EngagedBlocks();

    public void add(Chunk chunk, boolean additive) {
        if (additive) {
            chunk.sides.getSidePositions().forEach(this::add);
            chunk.engagedBlocks.getPositionToBlock().forEach(this::engage);
        } else {
            chunk.sides.getSidePositions().forEach(this::remove);
            chunk.engagedBlocks.getPositionToBlock().keySet().forEach(e -> this.engage(e, Block.AIR));
        }
    }

    public boolean isEmpty() {
        return getSidesToAdd().isEmpty() && getSidesToRemove().isEmpty();
    }

    public void add(SidePosition side) {
        getSidesToAdd().add(side);
    }

    public void remove(SidePosition sidePosition) {
        getSidesToRemove().add(sidePosition);
    }

    public void engage(Vector3ic pos, Block block) {
        getBlocksToChange().put(pos, block);
    }

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getEntityClientRepository().getEntityByClass(EntitySides.class).getBlockSidesUpdateClientPackets().add(this);
    }

}
