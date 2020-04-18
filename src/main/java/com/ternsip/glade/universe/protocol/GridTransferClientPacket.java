package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GridTransferClientPacket extends ClientPacket {

    private final byte[] gridCompressorBytes;

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getBlocksClientRepository().getGridBlocks().fromBytes(gridCompressorBytes);
        getUniverseClient().getBlocksClientRepository().getNeedRender().set(true);
    }
}
