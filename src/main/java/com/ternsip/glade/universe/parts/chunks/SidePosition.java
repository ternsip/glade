package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class SidePosition implements Serializable {

    private final int x;
    private final int y;
    private final int z;
    private final BlockSide side;

    public SidePosition(Indexer indexer, Integer value) {
        this.side = BlockSide.getSideByIndex(value % BlockSide.getSize());
        int idx = value / BlockSide.getSize();
        this.x = indexer.getX(idx);
        this.y = indexer.getY(idx);
        this.z = indexer.getZ(idx);
    }

    private int packToInteger(Indexer indexer) {
        return (int)indexer.getIndex(x, y, z) * BlockSide.getSize() + side.getIndex();
    }

}
