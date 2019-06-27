package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySides;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntitySides extends Entity<EffigySides> {

    @Override
    public void update(EffigySides effigy) {
        super.update(effigy);
        if (!getUniverse().getBlocks().getBlocksUpdates().isEmpty()) {
            effigy.applyBlockUpdate(getUniverse().getBlocks().getBlocksUpdates().poll());
        }
    }

    @Override
    public EffigySides getEffigy() {
        return new EffigySides();
    }

    @Override
    public void update() {
        super.update();
    }

}
