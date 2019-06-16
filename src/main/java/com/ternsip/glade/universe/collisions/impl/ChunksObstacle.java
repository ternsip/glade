package com.ternsip.glade.universe.collisions.impl;

import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Using A Fast Voxel Traversal Algorithm for Ray Tracing by John Amanatides and Andrew Woo
 */
@RequiredArgsConstructor
@Getter
@Slf4j
public class ChunksObstacle implements Obstacle, Universal {

    private static final int MAX_TRAVERSAL_LENGTH = 256;
    private static final float BLOCK_SAVE_DELTA = 1e-4f;
    private static final int CUBE_SIZE = 1;

    private final AABBf aabb = new AABBf(-Float.MAX_VALUE, 0, -Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

    @Override
    public Vector3fc collideSegment(LineSegmentf segment) {
        List<Vector3i> positions = traverse(segment, Block::isObstacle);
        Vector3fc start = new Vector3f(segment.aX, segment.aY, segment.aZ);
        Vector3f closest = new Vector3f(segment.bX, segment.bY, segment.bZ);
        for (Vector3i pos : positions) {
            AABBf aabb = new AABBf(
                    pos.x() * CUBE_SIZE - BLOCK_SAVE_DELTA, pos.y() * CUBE_SIZE - BLOCK_SAVE_DELTA, pos.z() * CUBE_SIZE - BLOCK_SAVE_DELTA,
                    (pos.x() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA, (pos.y() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA, (pos.z() + 1) * CUBE_SIZE + BLOCK_SAVE_DELTA
            );
            Vector3fc nPos = collideSegmentDefault(segment, aabb);
            if (nPos != null && start.distanceSquared(closest) > start.distanceSquared(nPos)) {
                closest.set(nPos);
            }
        }
        return closest;
    }

    private List<Vector3i> traverse(LineSegmentf segment, Function<Block, Boolean> condition) {

        List<Vector3i> positions = new ArrayList<>();

        // Ray direction
        Vector3fc ray = new Vector3f(
                segment.bX - segment.aX,
                segment.bY - segment.aY,
                segment.bZ - segment.aZ
        );

        // In which direction the voxel ids are incremented
        Vector3fc step = new Vector3f(
                (ray.x() >= 0) ? 1f : -1f,
                (ray.y() >= 0) ? 1f : -1f,
                (ray.z() >= 0) ? 1f : -1f
        );

        // Current voxel
        Vector3i currentVoxel = new Vector3i(
                (int) Math.floor(segment.aX / CUBE_SIZE),
                (int) Math.floor(segment.aY / CUBE_SIZE),
                (int) Math.floor(segment.aZ / CUBE_SIZE)
        );

        // Last voxel
        Vector3ic lastVoxel = new Vector3i(
                (int) Math.floor(segment.bX / CUBE_SIZE),
                (int) Math.floor(segment.bY / CUBE_SIZE),
                (int) Math.floor(segment.bZ / CUBE_SIZE)
        );

        // Distance along the ray to the next voxel border from the current position (tMaxX, tMaxY, tMaxZ)
        Vector3fc nextBoundary = new Vector3f(currentVoxel).add(step).mul(CUBE_SIZE);

        // tMax - distance until next intersection with voxel-border
        // the value of t at which the ray crosses the first vertical voxel boundary
        Vector3f tMax = new Vector3f(
                (Math.abs(ray.x()) != 0) ? (nextBoundary.x() - segment.aX) / ray.x() : Float.MAX_VALUE,
                (Math.abs(ray.y()) != 0) ? (nextBoundary.y() - segment.aY) / ray.y() : Float.MAX_VALUE,
                (Math.abs(ray.z()) != 0) ? (nextBoundary.z() - segment.aZ) / ray.z() : Float.MAX_VALUE
        );

        // How far along the ray we must move for the horizontal component to equal the width of a voxel
        // the direction in which we traverse the grid. Can only be Float.MAX_VALUE if we never go in that direction
        Vector3fc tDelta = new Vector3f(
                (Math.abs(ray.x()) != 0) ? CUBE_SIZE / ray.x() * step.x() : Float.MAX_VALUE,
                (Math.abs(ray.y()) != 0) ? CUBE_SIZE / ray.y() * step.y() : Float.MAX_VALUE,
                (Math.abs(ray.z()) != 0) ? CUBE_SIZE / ray.z() * step.z() : Float.MAX_VALUE
        );

        Vector3i diff = new Vector3i(0);
        boolean negativeRay = false;

        if (currentVoxel.x() != lastVoxel.x() && ray.x() < 0) {
            diff.x--;
            negativeRay = true;
        }
        if (currentVoxel.y() != lastVoxel.y() && ray.y() < 0) {
            diff.y--;
            negativeRay = true;
        }
        if (currentVoxel.z() != lastVoxel.z() && ray.z() < 0) {
            diff.z--;
            negativeRay = true;
        }

        if (checkVoxel(currentVoxel, condition)) {
            positions.add(new Vector3i(currentVoxel));
        }

        if (negativeRay) {
            currentVoxel.add(diff);
            if (checkVoxel(currentVoxel, condition)) {
                positions.add(new Vector3i(currentVoxel));
            }
        }

        int counter = 0;
        while (!lastVoxel.equals(currentVoxel)) {
            ++counter;
            if (tMax.x < tMax.y) {
                if (tMax.x < tMax.z) {
                    currentVoxel.x += step.x();
                    tMax.x += tDelta.x();
                } else {
                    currentVoxel.z += step.z();
                    tMax.z += tDelta.z();
                }
            } else {
                if (tMax.y < tMax.z) {
                    currentVoxel.y += step.y();
                    tMax.y += tDelta.y();
                } else {
                    currentVoxel.z += step.z();
                    tMax.z += tDelta.z();
                }
            }
            if (checkVoxel(currentVoxel, condition)) {
                positions.add(new Vector3i(currentVoxel));
            }
            if (counter > MAX_TRAVERSAL_LENGTH) {
                log.error("Potential loop inside chunks voxels traversal algorithm. Manual avoiding...");
                break;
            }
        }

        return positions;
    }

    private boolean checkVoxel(Vector3i pos, Function<Block, Boolean> condition) {
        return getUniverse().getChunks().isBlockLoaded(pos) && condition.apply(getUniverse().getChunks().getBlock(pos));
    }


}
