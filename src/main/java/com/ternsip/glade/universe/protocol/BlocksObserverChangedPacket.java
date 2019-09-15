package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class BlocksObserverChangedPacket extends ServerPacket {

    private final Vector3ic prevPos;
    private final Vector3ic nextPos;
    private final int prevViewDistance;
    private final int nextViewDistance;

    @Override
    public void apply(Connection connection) {
        getUniverseServer().getBlocksRepository().processMovement(getPrevPos(), getNextPos(), getPrevViewDistance(), getNextViewDistance());
    }
}
