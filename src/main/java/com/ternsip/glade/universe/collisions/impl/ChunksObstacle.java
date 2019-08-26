package com.ternsip.glade.universe.collisions.impl;

import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.*;

import java.util.List;

/**

 */
@RequiredArgsConstructor
@Getter
public class ChunksObstacle implements Obstacle, Universal {

    private static final float BLOCK_SAVE_DELTA = 1e-4f;
    private static final float CUBE_SIZE = 1;

    private final AABBf aabb = new AABBf(-Float.MAX_VALUE, 0, -Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

    @Override
    public Vector3fc collideSegment(LineSegmentf segment) {
        List<Vector3ic> positions = getUniverse().getBlocks().traverseFull(segment, block -> true);
        Vector3fc start = new Vector3f(segment.aX, segment.aY, segment.aZ);
        Vector3f closest = new Vector3f(segment.bX, segment.bY, segment.bZ);
        for (Vector3ic pos : positions) {
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dy = -1; dy <= 1; ++dy) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        Vector3fc nPos = collideBlockSafe(segment, pos.add(dx, dy, dz, new Vector3i()));
                        if (nPos != null && start.distanceSquared(closest) > start.distanceSquared(nPos)) {
                            closest.set(nPos);
                        }
                    }
                }
            }
        }
        return closest;
    }

    private Vector3fc collideBlockSafe(LineSegmentf segment, Vector3ic pos) {
        if (getUniverse().getBlocks().isBlockExists(pos) && !getUniverse().getBlocks().getBlock(pos).isObstacle()) {
            return null;
        }
        AABBf aabb = new AABBf(
                pos.x() * CUBE_SIZE - BLOCK_SAVE_DELTA, pos.y() * CUBE_SIZE - BLOCK_SAVE_DELTA, pos.z() * CUBE_SIZE - BLOCK_SAVE_DELTA,
                (pos.x() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA, (pos.y() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA, (pos.z() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA
        );
        return collideSegmentDefault(segment, aabb);
    }

}
