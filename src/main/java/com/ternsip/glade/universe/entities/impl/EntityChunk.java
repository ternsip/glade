package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.DynamicReloadable;
import com.ternsip.glade.graphics.visual.impl.chunk.EffigyChunk;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityChunk extends Entity<DynamicReloadable> {

    private final Chunk chunk;

    @Override
    public DynamicReloadable getVisual() {
        return new DynamicReloadable(() -> new EffigyChunk(chunk));
    }

    @Override
    public void update(DynamicReloadable visual) {
        if (getChunk().isVisualReloadRequired()) {
            visual.reload();
            getChunk().setLogicReloadRequired(false);
        }
    }

    @Override
    public void update() {
        getChunk().update();
    }

}
