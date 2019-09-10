package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.Packet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class BlocksObserverChangedPacket implements Packet {

    private final Vector3ic prevPos;
    private final Vector3ic nextPos;
    private final int prevViewDistance;
    private final int nextViewDistance;

    @Override
    public void apply(Connection connection) {
        getUniverse().getBlocks().processMovement(getPrevPos(), getNextPos(), getPrevViewDistance(), getNextViewDistance());
    }
}
