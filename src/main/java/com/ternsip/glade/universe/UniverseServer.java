package com.ternsip.glade.universe;

import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyAxis;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyDynamicText;
import com.ternsip.glade.graphics.visual.impl.test.*;
import com.ternsip.glade.universe.collisions.impl.ChunksObstacle;
import com.ternsip.glade.universe.collisions.impl.GroundObstacle;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.EntityGeneric;
import com.ternsip.glade.universe.entities.base.EntityGenericRotating;
import com.ternsip.glade.universe.entities.impl.EntityCubeSelection;
import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import com.ternsip.glade.universe.interfaces.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@Getter
@Setter
public class UniverseServer implements Threadable, INetworkServer, IBlocksRepository, ICollisions, IBalance, IEntityServerRepository {

    @Override
    public void init() {
        startServer();
        spawnEntities();
    }

    @Override
    public void update() {
        getEntityServerRepository().update();
        getCollisions().update();
        getServer().getNetworkServerEventReceiver().update();
    }

    @SneakyThrows
    @Override
    public void finish() {
        stopBlocksThread();
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

        EntitySun sun = new EntitySun();
        sun.register();

        Entity cube = new EntityGeneric(() -> new EffigyCube());
        cube.register();

        Entity lamp = new EntityGeneric(() -> new EffigyLamp());
        lamp.register();
        lamp.register();
        lamp.setPosition(new Vector3f(-60f, 0, -60));
        lamp.setScale(new Vector3f(40, 40, 40));

        Entity bottle = new EntityGeneric(() -> new EffigyBottle());
        bottle.register();
        bottle.setPosition(new Vector3f(-30f, 0, -20));
        bottle.setScale(new Vector3f(5, 5, 5));

        Entity zebra = new EntityGeneric(() -> new EffigyZebra());
        zebra.register();
        zebra.setPosition(new Vector3f(-20f, 0, -20));
        zebra.setScale(new Vector3f(30, 30, 30));

        Entity wolf = new EntityGeneric(() -> new EffigyWolf());
        wolf.register();
        wolf.setPosition(new Vector3f(-140f, 0, -40));
        wolf.setScale(new Vector3f(30, 30, 30));

        Entity hagrid = new EntityGenericRotating(() -> new EffigyHagrid(), new Vector3f(0, 0.01f, 0));
        hagrid.register();
        hagrid.setPosition(new Vector3f(20f, 2, 2));
        hagrid.setScale(new Vector3f(15, 15, 15));

        Entity spider = new EntityGeneric(() -> new EffigySpider());
        spider.register();
        spider.setPosition(new Vector3f(20f, 2, -20));
        spider.setScale(new Vector3f(5, 5, 5));

        Entity warrior = new EntityGeneric(() -> new EffigyWarrior());
        warrior.register();
        warrior.setPosition(new Vector3f(-20f, 2, 2));
        warrior.setScale(new Vector3f(10, 10, 10));

        Entity dude = new EntityGeneric(() -> new EffigyDude());
        dude.register();
        dude.setPosition(new Vector3f(-20f, 0, -20));
        dude.setScale(new Vector3f(10f, 10f, 10f));

        new EntityGenericRotating(() -> new EffigyDynamicText(new File("fonts/default.png"), false, false, new Vector4f(0, 0, 1, 1), "Hello world!"), new Vector3f(0, 0.1f, 0)).register();

        getCollisions().add(new GroundObstacle());
        getCollisions().add(new ChunksObstacle());

        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                Entity hagrid1 = new EntityGenericRotating(() -> new EffigyHagrid(), new Vector3f(0, 0.01f, 0));
                hagrid1.register();
                hagrid1.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                hagrid1.setScale(new Vector3f(15, 15, 15));
            }
        }

        // TODO make it client-side
        new EntityGeneric(() -> new EffigyAxis()).register();

        EntityPlayer entityPlayer = new EntityPlayer();
        entityPlayer.register();
        entityPlayer.setPosition(new Vector3f(50, 90, 50));
        entityPlayer.setScale(new Vector3f(1, 1, 1));
        getEntityServerRepository().setCameraTarget(entityPlayer);

        EntityCubeSelection entityCubeSelection = new EntityCubeSelection(entityPlayer);
        entityCubeSelection.register();
    }

}
