package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3i;

@RequiredArgsConstructor
@Getter
public class GridTransferClientPacket extends ClientPacket {

    private final byte[] gridCompressorBytes;

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getBlocksClientRepository().getGridBlocks().fromBytes(gridCompressorBytes);
        getUniverseClient().getBlocksClientRepository().visualUpdate(new Vector3i(0, 0, 0), new Vector3i(256, 256, 256)); // TODO remove this
    }
}
