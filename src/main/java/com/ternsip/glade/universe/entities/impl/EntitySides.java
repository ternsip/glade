package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySides;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.parts.chunks.ChangeBlocksRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3i;

import java.util.concurrent.ConcurrentLinkedDeque;

@RequiredArgsConstructor
@Getter
@Setter
public class EntitySides extends GraphicalEntity<EffigySides> {

    private transient final ConcurrentLinkedDeque<ChangeBlocksRequest> changeBlocksRequests = new ConcurrentLinkedDeque<>();

    private final Vector3i observerPos = new Vector3i(-1000);
    private int observerViewDistance = 0;

    @Override
    public void update(EffigySides effigy) {
        super.update(effigy);
        while (!getChangeBlocksRequests().isEmpty()) {
            effigy.applyBlockUpdate(getChangeBlocksRequests().poll());
        }
    }

    @Override
    public EffigySides getEffigy() {
        return new EffigySides();
    }

}
