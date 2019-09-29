package com.ternsip.glade.universe.collisions.impl;

import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.AABBf;
import org.joml.LineSegmentf;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

import javax.annotation.Nullable;

@RequiredArgsConstructor
@Getter
public class ChunksObstacle implements Obstacle, IUniverseServer {

    private static final float BLOCK_SAVE_DELTA = 1e-4f;
    private static final float CUBE_SIZE = 1;

    private final AABBf aabb = new AABBf(-Float.MAX_VALUE, 0, -Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

    @Nullable
    @Override
    public Vector3fc collideSegment(LineSegmentf segment) {
        Vector3ic pos = getUniverseServer().getBlocksRepository().traverse(segment, Block::isObstacle);
        if (pos == null) {
            return null;
        }
        AABBf aabb = new AABBf(
                pos.x() * CUBE_SIZE - BLOCK_SAVE_DELTA, pos.y() * CUBE_SIZE - BLOCK_SAVE_DELTA, pos.z() * CUBE_SIZE - BLOCK_SAVE_DELTA,
                (pos.x() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA, (pos.y() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA, (pos.z() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA
        );
        return collideSegmentDefault(segment, aabb);
    }

}
