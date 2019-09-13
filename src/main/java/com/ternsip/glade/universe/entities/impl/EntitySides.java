package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.visual.impl.basis.EffigySides;
import com.ternsip.glade.network.ClientSide;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.protocol.BlocksObserverChangedPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
@Setter
@ClientSide
public class EntitySides extends Entity<EffigySides> {

    private final Timer timer = new Timer(250);

    private final Vector3i observerPos = new Vector3i(-1000);
    private int observerViewDistance = 0;

    @Override
    public void register() {
        super.register();
        moveObserver();
    }

    @Override
    public void update(EffigySides effigy) {
        super.update(effigy);
        // TODO fix critical bug using server on client (you can see unloaded chunks)
        if (!getUniverseServer().getBlocks().getBlocksUpdates().isEmpty()) {
            effigy.applyBlockUpdate(getUniverseServer().getBlocks().getBlocksUpdates().poll());
        }
    }

    @Override
    public EffigySides getEffigy() {
        return new EffigySides();
    }

    @Override
    public void clientUpdate() {
        super.clientUpdate();
        if (getTimer().isOver()) {
            moveObserver();
            getTimer().drop();
        }
    }

    private void moveObserver() {
        // TODO sometimes it crashes with nullpointer
        Vector3fc cameraPos = getUniverseClient().getEntityClientRepository().getCameraTarget().getPosition();
        Vector3ic newPos = new Vector3i((int) cameraPos.x(), (int) cameraPos.y(), (int) cameraPos.z());
        int viewDistance = 4; // TODO receive this from client options (make options class)
        if (!getObserverPos().equals(newPos) || getObserverViewDistance() != viewDistance) {
            getUniverseClient().getClient().send(new BlocksObserverChangedPacket(new Vector3i(getObserverPos()), newPos, getObserverViewDistance(), viewDistance));
            getObserverPos().set(newPos);
            setObserverViewDistance(viewDistance);
        }
    }

}
