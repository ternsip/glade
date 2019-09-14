package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.parts.chunks.BlocksUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class BlocksUpdatePacket extends ClientPacket {

    private final List<BlocksUpdate> blocksUpdate;

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getEntityClientRepository().getEntitySides().getBlocksUpdates().addAll(getBlocksUpdate());
    }
}
