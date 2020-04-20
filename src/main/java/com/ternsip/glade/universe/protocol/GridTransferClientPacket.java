package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.storage.Storage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GridTransferClientPacket extends ClientPacket {

    private final byte[] storageData;

    @Override
    public void apply(Connection connection) {
        getUniverseClient().getBlocksClientRepository().setStorage(Storage.fromBytes(storageData, "client_world"));
    }

}
