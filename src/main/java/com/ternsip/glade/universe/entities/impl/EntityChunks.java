package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigyChunks;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityChunks extends Entity<EffigyChunks> {

    @Override
    public EffigyChunks getEffigy() {
        return new EffigyChunks(256);
    }

    @Override
    public void update(EffigyChunks effigy) {
        super.update(effigy);
        if (!getUniverse().getChunks().getBlocksUpdates().isEmpty()) {
            effigy.recalculateBlockRegion(getUniverse().getChunks().getBlocksUpdates().poll());
        }
    }
}
