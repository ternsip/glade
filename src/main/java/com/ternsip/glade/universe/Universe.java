package com.ternsip.glade.universe;

import com.ternsip.glade.common.events.base.EventSnapReceiver;
import com.ternsip.glade.graphics.visual.impl.basis.Effigy3DText;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyAxis;
import com.ternsip.glade.graphics.visual.impl.test.*;
import com.ternsip.glade.universe.collisions.base.Collisions;
import com.ternsip.glade.universe.collisions.impl.ChunksObstacle;
import com.ternsip.glade.universe.collisions.impl.GroundObstacle;
import com.ternsip.glade.universe.common.Balance;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.*;
import com.ternsip.glade.universe.entities.repository.EntityRepository;
import com.ternsip.glade.universe.parts.chunks.Chunks;
import lombok.Getter;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.File;

@Getter
public class Universe {

    private final EventSnapReceiver eventSnapReceiver = new EventSnapReceiver();
    private final EntityRepository entityRepository = new EntityRepository();
    private final Chunks chunks = new Chunks();
    private final String name = "universe";
    private final Balance balance = new Balance();
    private final Collisions collisions = new Collisions();

    public void initialize() {
        spawnTestEntities();
        generateChunks();
    }

    @SneakyThrows
    public void loop() {
        while (getEventSnapReceiver().isApplicationActive()) {
            long startTime = System.currentTimeMillis();
            update();
            long pastTime = System.currentTimeMillis() - startTime;
            long needToSleep = (long) Math.max(1000.0f / getBalance().getTicksPerSecond() - pastTime, 0);
            if (needToSleep > 0) {
                Thread.sleep(needToSleep);
            }
        }
    }

    public void finish() {
        getChunks().finish();
    }

    private void spawnTestEntities() {
        EntityPlayer entityPlayer = new EntityPlayer();
        entityPlayer.register();
        entityPlayer.setPosition(new Vector3f(50, 90, 50));
        entityPlayer.setScale(new Vector3f(1, 1, 1));
        getEntityRepository().setCameraTarget(entityPlayer);

        new EntitySun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1)).register();

        Entity cube = new EntityGeneric(() -> new EffigyCube());
        cube.register();

        Entity lamp = new EntityGeneric(() -> new EffigyLamp());
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

        new EntityGenericRotating(() -> new Effigy3DText(new File("fonts/default.png"), "Hello world!"), new Vector3f(0, 0.1f, 0)).register();
        new EntityGeneric(() -> new EffigyAxis()).register();

        new EntityStatistics().register();

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
    }

    private void generateChunks() {
        new EntityChunks().register();
        for (int cx = 0; cx < 16; ++cx) {
            for (int cy = 0; cy < 8; ++cy) {
                for (int cz = 0; cz < 16; ++cz) {
                    getChunks().getChunk(new Vector3i(cx, cy, cz));
                }
            }
        }
        getChunks().recalculateBlockRegion(new Vector3i(0, 0, 0), new Vector3i(16 * 16, 16 * 8, 16 * 16));
    }

    private void update() {
        getEventSnapReceiver().update();
        getEntityRepository().update();
        getCollisions().update();
    }

}
