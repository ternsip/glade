package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.storage.Storage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@Setter
public class BlocksClientRepository extends BlocksRepositoryBase implements IUniverseClient {

    private final ConcurrentLinkedDeque<SidesUpdate> sidesUpdates = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean needRender = new AtomicBoolean(false);
    private volatile Vector3ic observingPos = new Vector3i(-1000);
    private Storage storage;

    @Override
    protected void onSidesUpdate(SidesUpdate sidesUpdate) {
        sidesUpdates.add(sidesUpdate);
    }

}
