package com.ternsip.glade.universe;

import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyDynamicText;
import com.ternsip.glade.graphics.visual.impl.test.*;
import com.ternsip.glade.network.INetworkServerEventReceiver;
import com.ternsip.glade.universe.collisions.impl.ChunksObstacle;
import com.ternsip.glade.universe.collisions.impl.GroundObstacle;
import com.ternsip.glade.universe.entities.base.EntityGenericServer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerListServer;
import com.ternsip.glade.universe.entities.impl.EntitySunServer;
import com.ternsip.glade.universe.interfaces.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@Getter
@Setter
public class UniverseServer implements Threadable, INetworkServer, IBlocksRepositoryServer, ICollisionsServer, IBalance, IEntityServerRepository, INetworkServerEventReceiver {

    @Override
    public void init() {
        spawnEntities();
        IBlocksRepositoryServer.BLOCKS_SERVER_REPOSITORY_THREAD.touch();
    }

    @Override
    public void update() {
        getEntityServerRepository().update();
        getCollisionsServer().update();
        getNetworkServerEventReceiver().update();
    }

    @SneakyThrows
    @Override
    public void finish() {
        stopBlocksServerThread();
        getEntityServerRepository().finish();
        stopServerThread();
    }

    public void startServer() {
        getServer().bind(6789);
    }

    public void stop() {
        IUniverseServer.stopUniverseServerThread();
    }

    private void spawnEntities() {

        EntitySunServer sun = new EntitySunServer();
        sun.register();

        EntityGenericServer cube = new EntityGenericServer(EffigyCube::new);
        cube.register();

        EntityGenericServer lamp = new EntityGenericServer(EffigyLamp::new);
        lamp.register();
        lamp.register();
        lamp.setPosition(new Vector3f(-60f, 0, -60));
        lamp.setScale(new Vector3f(40, 40, 40));

        EntityGenericServer bottle = new EntityGenericServer(EffigyBottle::new);
        bottle.register();
        bottle.setPosition(new Vector3f(-30f, 0, -20));
        bottle.setScale(new Vector3f(5, 5, 5));

        EntityGenericServer zebra = new EntityGenericServer(EffigyZebra::new);
        zebra.register();
        zebra.setPosition(new Vector3f(-20f, 0, -20));
        zebra.setScale(new Vector3f(30, 30, 30));

        EntityGenericServer wolf = new EntityGenericServer(EffigyWolf::new);
        wolf.register();
        wolf.setPosition(new Vector3f(-140f, 0, -40));
        wolf.setScale(new Vector3f(30, 30, 30));

        EntityGenericServer hagrid = new EntityGenericServer(EffigyHagrid::new);
        hagrid.register();
        hagrid.setPosition(new Vector3f(-10f, 0, -10));
        hagrid.setScale(new Vector3f(8, 8, 8));

        EntityGenericServer spider = new EntityGenericServer(EffigySpider::new);
        spider.register();
        spider.setPosition(new Vector3f(20f, 2, -20));
        spider.setScale(new Vector3f(5, 5, 5));

        EntityGenericServer warrior = new EntityGenericServer(EffigyWarrior::new);
        warrior.register();
        warrior.setPosition(new Vector3f(-20f, 2, 2));
        warrior.setScale(new Vector3f(10, 10, 10));

        EntityGenericServer dude = new EntityGenericServer(EffigyDude::new);
        dude.register();
        dude.setPosition(new Vector3f(-20f, 0, -20));
        dude.setScale(new Vector3f(10f, 10f, 10f));

        new EntityGenericServer(() -> new EffigyDynamicText(new File("fonts/default.png"), false, false, new Vector4f(0, 0, 1, 1), "Hello world!"), new Vector3f(0, 0.1f, 0)).register();

        getCollisionsServer().add(new GroundObstacle());
        getCollisionsServer().add(new ChunksObstacle(getBlocksServerRepository()));

        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                EntityGenericServer hagrid1 = new EntityGenericServer(EffigyHagrid::new, new Vector3f(0, 0.01f, 0));
                hagrid1.register();
                hagrid1.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                hagrid1.setScale(new Vector3f(15, 15, 15));
            }
        }

        new EntityPlayerListServer().register();

    }

}
