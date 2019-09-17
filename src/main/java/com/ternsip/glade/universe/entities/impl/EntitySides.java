package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySides;
import com.ternsip.glade.network.ClientSide;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.chunks.BlocksUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3i;

import java.util.concurrent.ConcurrentLinkedDeque;

@RequiredArgsConstructor
@Getter
@Setter
@ClientSide
public class EntitySides extends Entity<EffigySides> {

    private transient final ConcurrentLinkedDeque<BlocksUpdate> blocksUpdates = new ConcurrentLinkedDeque<>();

    private final Vector3i observerPos = new Vector3i(-1000);
    private int observerViewDistance = 0;

    @Override
    public void update(EffigySides effigy) {
        super.update(effigy);
        if (!getBlocksUpdates().isEmpty()) {
            effigy.applyBlockUpdate(getBlocksUpdates().poll());
        }
    }

    @Override
    public EffigySides getEffigy() {
        return new EffigySides();
    }

}
