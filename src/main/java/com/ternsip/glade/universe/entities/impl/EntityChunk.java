package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyChunk;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.AABBf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

@RequiredArgsConstructor
@Getter
public class EntityChunk extends Entity {

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

    //@Override
    //public Vector3fc collideSegment(LineSegmentf segment) {
    //    return collideSegmentDefault(segment);
    //}

}
