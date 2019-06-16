package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyChunk;
import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.*;

import java.lang.Math;
import java.util.function.Function;

import static com.ternsip.glade.common.logic.Maths.EPS;

@RequiredArgsConstructor
@Getter
public class EntityChunk extends Entity implements Obstacle {

    private final Chunk chunk;
    private final AABBf aabb;

    public EntityChunk(Chunk chunk) {
        this.chunk = chunk;
        Vector3fc start = new Vector3f(getChunk().getPosition().mul(Chunk.SIZE, new Vector3i()));
        Vector3fc end = new Vector3f(Chunk.SIZE).add(start);
        this.aabb = new AABBf(start, end);
    }

    @Override
    public Effigy getEffigy() {
        return new EffigyChunk(getChunk());
    }

    @Override
    public void update() {
        getChunk().update();
    }

    @Override
    public boolean isVisualReloadRequired() {
        return getChunk().isVisualReloadRequired();
    }

    @Override
    public void setVisualReloadRequired(boolean visualReloadRequired) {
        getChunk().setVisualReloadRequired(visualReloadRequired);
    }

    @Override
    public Vector3fc collideSegment(LineSegmentf segment) {
        Vector3fc start = new Vector3f(getChunk().getPosition().mul(Chunk.SIZE, new Vector3i()));
        LineSegmentf modifiedSegment = new LineSegmentf(
                segment.aX - start.x(), segment.aY - start.y(), segment.aZ - start.z(),
                segment.bX - start.x(), segment.bY - start.y(), segment.bZ - start.z()
        );

        Vector3i pos = findFirstBlock(modifiedSegment, Block::isObstacle);
        if (pos == null) {
            return new Vector3f(segment.bX, segment.bY, segment.bZ);
        }
        AABBf aabb = new AABBf(
                start.x() + pos.x(), start.y() + pos.y(), start.z() + pos.z(),
                start.x() + pos.x() + 1, start.y() + pos.y() + 1, start.z() + pos.z() + 1
        );
        return collideSegmentDefault(segment, aabb);
    }

    private Vector3i findFirstBlock(LineSegmentf segment, Function<Block, Boolean> condition) {

        float cubeSize = 1;

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
                (int) Math.floor(segment.aX / cubeSize),
                (int) Math.floor(segment.aY / cubeSize),
                (int) Math.floor(segment.aZ / cubeSize)
        );

        // Last voxel
        Vector3ic lastVoxel = new Vector3i(
                (int) Math.floor(segment.bX / cubeSize),
                (int) Math.floor(segment.bY / cubeSize),
                (int) Math.floor(segment.bZ / cubeSize)
        );

        // Distance along the ray to the next voxel border from the current position (tMaxX, tMaxY, tMaxZ)
        Vector3fc nb = new Vector3f(currentVoxel).add(step).mul(cubeSize);

        // tMaxX, tMaxY, tMaxZ -- distance until next intersection with voxel-border
        // the value of t at which the ray crosses the first vertical voxel boundary
        Vector3f tMax = new Vector3f(
                (Math.abs(ray.x()) > EPS) ? (nb.x() - segment.aX) / ray.x() : Float.MAX_VALUE,
                (Math.abs(ray.y()) > EPS) ? (nb.y() - segment.aY) / ray.y() : Float.MAX_VALUE,
                (Math.abs(ray.z()) > EPS) ? (nb.z() - segment.aZ) / ray.z() : Float.MAX_VALUE
        );

        // How far along the ray we must move for the horizontal component to equal the width of a voxel
        // the direction in which we traverse the grid. Can only be Float.MAX_VALUE if we never go in that direction
        Vector3fc tDelta = new Vector3f(
                (Math.abs(ray.x()) > EPS) ? cubeSize / ray.x() * step.x() : Float.MAX_VALUE,
                (Math.abs(ray.y()) > EPS) ? cubeSize / ray.y() * step.y() : Float.MAX_VALUE,
                (Math.abs(ray.z()) > EPS) ? cubeSize / ray.z() * step.z() : Float.MAX_VALUE
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
            return currentVoxel;
        }

        if (negativeRay) {
            currentVoxel.add(diff);
            if (checkVoxel(currentVoxel, condition)) {
                return currentVoxel;
            }
        }

        while (!lastVoxel.equals(currentVoxel)) {
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
                return currentVoxel;
            }
        }

        return null;
    }

    private boolean checkVoxel(Vector3i pos, Function<Block, Boolean> condition) {
        return pos.x >= 0 && pos.y >= 0 && pos.z >= 0 &&
                pos.x < Chunk.SIZE && pos.y < Chunk.SIZE && pos.z < Chunk.SIZE &&
                condition.apply(getChunk().getBlock(pos));
    }


}
