package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@Getter
public class SetBlocksRequest {

    private final Vector3ic start;
    private final Block[][][] blocks;

    public SetBlocksRequest(Vector3ic start, Block[][][] blocks) {
        this.start = start;
        this.blocks = blocks;
        if (getSize().length() == 0) {
            throw new IllegalArgumentException("Region should not have an empty volume");
        }
    }

    public SetBlocksRequest(Vector3ic start, Block block) {
        this.start = start;
        this.blocks = new Block[1][1][1];
        this.blocks[0][0][0] = block;
    }

    public Vector3i getEndExcluding() {
        return new Vector3i(start).add(getSize());
    }

    public Vector3i getSize() {
        return new Vector3i(blocks.length, blocks[0].length, blocks[0][0].length);
    }

}