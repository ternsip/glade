package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.visual.impl.basis.EffigySides;
import com.ternsip.glade.network.requests.BlocksObserverChanged;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class EntitySides extends Entity<EffigySides> {

    private final Timer timer = new Timer(250);

    private final Vector3i observerPos = new Vector3i(-1000);

    @Override
    public void register() {
        super.register();
        moveObserver();
    }

    @Override
    public void update(EffigySides effigy) {
        super.update(effigy);
        if (getTimer().isOver()) {
            moveObserver();
            getTimer().drop();
        }
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

    private void moveObserver() {
        Vector3ic newPos = getCameraPosition();
        if (!getObserverPos().equals(newPos)) {
            getUniverse().getClient().send(new BlocksObserverChanged(getObserverPos(), newPos));
        }
        getObserverPos().set(newPos);
    }

    private Vector3ic getCameraPosition() {
        Vector3fc pos = getUniverse().getEntityRepository().getCameraTarget().getPosition();
        return new Vector3i((int)pos.x(), (int)pos.y(), (int)pos.z());
    }

}
