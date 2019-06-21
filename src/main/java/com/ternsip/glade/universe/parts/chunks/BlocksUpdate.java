package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class BlocksUpdate {

    private final Block[][][] blocks;
    private final int[][] heights;
    private final Vector3ic start;
    private final boolean forceUpdate;

}