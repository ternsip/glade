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
        return new EffigyChunks();
    }

    @Override
    public void update(EffigyChunks effigy) {
        super.update(effigy);
        effigy.update(getUniverse().getChunks().getPositionsToUpdate());
        getUniverse().getChunks().getPositionsToUpdate().clear();
    }
}
