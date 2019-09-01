package com.ternsip.glade.universe;

import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.universe.collisions.impl.ChunksObstacle;
import com.ternsip.glade.universe.collisions.impl.GroundObstacle;
import com.ternsip.glade.universe.interfaces.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter
@Setter
public class UniverseServer implements Threadable, INetworkServer, IBlocksRepository, ICollisions,
        IBalance, IEntityRepository, IEventSnapReceiver {

    private final String name = "universe";

    @Override
    public void init() {
        getCollisions().add(new GroundObstacle());
        getCollisions().add(new ChunksObstacle());
    }

    @Override
    public void update() {
        if (!getEventSnapReceiver().isApplicationActive()) {
            IUniverseServer.stopUniverseServerThread();
        }
        getEntityRepository().update();
        getCollisions().update();
    }

    @SneakyThrows
    @Override
    public void finish() {
        stopServerThread();
        stopBlocksThread();
    }

}
