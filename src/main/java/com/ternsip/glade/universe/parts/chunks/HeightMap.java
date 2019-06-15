package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.Serializable;

@Getter
// TODO rename to slice
public class HeightMap implements Serializable, Universal {

    private final Vector2ic position;

    private final Heights[][] heights = new Heights[Chunk.SIZE][Chunk.SIZE];

    public HeightMap(Vector2ic position) {
        this.position = position;
        for (int x = 0; x < Chunk.SIZE; ++x) {
            for (int z = 0; z < Chunk.SIZE; ++z) {
                heights[x][z] = new Heights(0, 0, 0, 0);
            }
        }
    }

    public Heights getHeights(Vector2i position) {
        return getHeights()[position.x()][position.y()];
    }

}
