package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigyChunks;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.chunks.BlocksUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@RequiredArgsConstructor
@Getter
public class EntityChunks extends Entity<EffigyChunks> {

    @Getter(AccessLevel.PUBLIC)
    private final Deque<BlocksUpdate> blocksUpdates = new ConcurrentLinkedDeque<>();

    @Override
    public void update(EffigyChunks effigy) {
        super.update(effigy);
        if (!getUniverse().getBlocks().getBlocksUpdates().isEmpty()) {
            effigy.recalculateBlockRegion(getUniverse().getBlocks().getBlocksUpdates().poll());
        }
    }

    @Override
    public EffigyChunks getEffigy() {
        return new EffigyChunks();
    }

    @Override
    public void update() {
        super.update();
    }

}
