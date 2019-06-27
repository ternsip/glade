package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BlocksUpdate {

    private final BlockChanges rigidSides = new BlockChanges();
    private final BlockChanges translucentSides = new BlockChanges();
    private final BlockChanges waterSides = new BlockChanges();

    public BlocksUpdate(Sides sides) {
        sides.getSides().forEach((pos, data) -> {
            add(new Side(pos, data));
        });
    }

    public void add(Side side) {
        getSideChanges(side.getSideData().getBlock()).getSidesToAdd().add(side);
    }

    public void remove(Block block, SidePosition sidePosition) {
        getSideChanges(block).getSidesToRemove().add(sidePosition);
    }

    public BlockChanges getSideChanges(Block block) {
        if (block == Block.WATER) {
            return getWaterSides();
        } else if (block.isTranslucent()) {
            return getTranslucentSides();
        } else {
            return getRigidSides();
        }
    }

    public boolean isEmpty() {
        return getRigidSides().isEmpty() && getTranslucentSides().isEmpty() && getWaterSides().isEmpty();
    }

}