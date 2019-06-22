package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Transformable;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyChunks;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3i;

@RequiredArgsConstructor
@Getter
public class EntityChunks extends Entity<EffigyChunks> {

    private final Vector3i shift;
    private final Transformable target;
    private final int viewDistanceChunks;
    private final int viewDistanceBlocks;

    public EntityChunks(Transformable target, int viewDistanceChunks) {
        this.shift = getTargetShift(target, viewDistanceChunks);
        this.target = target;
        this.viewDistanceChunks = viewDistanceChunks;
        this.viewDistanceBlocks = viewDistanceChunks * Chunk.SIZE;
        getUniverse().getChunks().recalculateBlockRegion(
                new Vector3i(shift),
                new Vector3i(viewDistanceBlocks, viewDistanceBlocks, viewDistanceBlocks),
                true
        );
    }

    @Override
    public EffigyChunks getEffigy() {
        return new EffigyChunks(getViewDistanceBlocks());
    }

    @Override
    public void update() {
        super.update();
        Vector3i newShift = getTargetShift(getTarget(), getViewDistanceChunks());
        if (!newShift.equals(getShift())) {

            Vector3i oldShift = getShift();
            Vector3i oldEndExclusive = new Vector3i(oldShift).add(new Vector3i(getViewDistanceBlocks()));
            Vector3i newEndExclusive = new Vector3i(newShift).add(new Vector3i(getViewDistanceBlocks()));

            getShift().set(newShift);

            int dx = newShift.x() - oldShift.x();
            int dy = newShift.y() - oldShift.y();
            int dz = newShift.z() - oldShift.z();
            if (dx > 0) {
                getUniverse().getChunks().recalculateBlockRegion(
                        new Vector3i(oldEndExclusive.x(), newShift.y(), newShift.z()),
                        new Vector3i(newEndExclusive.x() - oldEndExclusive.x(), getViewDistanceBlocks(), getViewDistanceBlocks()),
                        true
                );
            } else {
                getUniverse().getChunks().recalculateBlockRegion(
                        new Vector3i(newShift.x(), newShift.y(), newShift.z()),
                        new Vector3i(oldShift.x() - newShift.x(), getViewDistanceBlocks(), getViewDistanceBlocks()),
                        true
                );
            }

            int boundMinX = Math.max(Math.min(newShift.x(), oldEndExclusive.x() - 1), oldShift.x());
            int boundMaxX = Math.max(Math.min(newEndExclusive.x() - 1, oldEndExclusive.x() - 1), oldShift.x());
            int boundSizeX = boundMaxX - boundMinX + 1;
            if (dy > 0) {
                getUniverse().getChunks().recalculateBlockRegion(
                        new Vector3i(boundMinX, oldEndExclusive.y(), newShift.z()),
                        new Vector3i(boundSizeX, newEndExclusive.y() - oldEndExclusive.y(), getViewDistanceBlocks()),
                        true
                );
            } else {
                getUniverse().getChunks().recalculateBlockRegion(
                        new Vector3i(boundMinX, newShift.y(), newShift.z()),
                        new Vector3i(boundSizeX, oldShift.y() - newShift.y(), getViewDistanceBlocks()),
                        true
                );
            }

            int boundMinY = Math.max(Math.min(newShift.y(), oldEndExclusive.y() - 1), oldShift.y());
            int boundMaxY = Math.max(Math.min(newEndExclusive.y() - 1, oldEndExclusive.y() - 1), oldShift.y());
            int boundSizeY = boundMaxY - boundMinY + 1;
            if (dz > 0) {
                getUniverse().getChunks().recalculateBlockRegion(
                        new Vector3i(boundMinX, boundMinY, newShift.z()),
                        new Vector3i(boundSizeX, boundSizeY, newEndExclusive.z() - oldEndExclusive.z()),
                        true
                );
            } else {
                getUniverse().getChunks().recalculateBlockRegion(
                        new Vector3i(boundMinX, boundMinY, newShift.z()),
                        new Vector3i(boundSizeX, boundSizeY, oldShift.z() - newShift.z()),
                        true
                );
            }

        }
    }

    @Override
    public void update(EffigyChunks effigy) {
        super.update(effigy);
        effigy.getShift().set(getShift());
        if (!getUniverse().getChunks().getBlocksUpdates().isEmpty()) {
            effigy.recalculateBlockRegion(getUniverse().getChunks().getBlocksUpdates().poll());
        }
    }

    private static Vector3i getTargetShift(Transformable transformable, int viewDistanceChunks) {
        return new Vector3i(
                Chunk.SIZE * (int) (transformable.getPosition().x() / Chunk.SIZE - viewDistanceChunks * 0.5f),
                Chunk.SIZE * (int) (transformable.getPosition().y() / Chunk.SIZE - viewDistanceChunks * 0.5f),
                Chunk.SIZE * (int) (transformable.getPosition().z() / Chunk.SIZE - viewDistanceChunks * 0.5f)
        );
    }
}
