package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyChunk;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityChunk extends Entity {

    private final Chunk chunk;

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

}
