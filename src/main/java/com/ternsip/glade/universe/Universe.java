package com.ternsip.glade.universe;

import com.ternsip.glade.common.events.base.EventSnapReceiver;
import com.ternsip.glade.graphics.visual.impl.basis.Effigy3DText;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyAxis;
import com.ternsip.glade.graphics.visual.impl.chunk.EffigyChunk;
import com.ternsip.glade.graphics.visual.impl.test.*;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.EntityTransformable;
import com.ternsip.glade.universe.entities.impl.*;
import com.ternsip.glade.universe.entities.repository.EntityRepository;
import com.ternsip.glade.universe.parts.blocks.Chunks;
import com.ternsip.glade.universe.storage.Storage;
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
    private final Storage universeStorage = new Storage("universe");
    private final Chunks chunks = new Chunks();
    private int ticksPerSecond = 128; // TODO move into universe settings

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
            long needToSleep = (long) Math.max(1000.0f / ticksPerSecond - pastTime, 0);
            if (needToSleep > 0) {
                Thread.sleep(needToSleep);
            }
        }
    }

    public void finish() {
        getUniverseStorage().commit();
        getUniverseStorage().finish();
    }

    private void spawnTestEntities() {
        EntityPlayer entityPlayer = new EntityPlayer();
        entityPlayer.setScale(new Vector3f(5, 5, 5));
        getEntityRepository().setCameraTarget(entityPlayer);

        new EntitySun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        EntityGeneric cube = new EntityGeneric(() -> new EffigyCube());

        EntityGeneric lamp = new EntityGeneric(() -> new EffigyLamp());
        lamp.setPosition(new Vector3f(-60f, 0, -60));
        lamp.setScale(new Vector3f(40, 40, 40));

        EntityGeneric bottle = new EntityGeneric(() -> new EffigyBottle());
        bottle.setPosition(new Vector3f(-30f, 0, -20));
        bottle.setScale(new Vector3f(5, 5, 5));

        EntityGeneric zebra = new EntityGeneric(() -> new EffigyZebra());
        zebra.setPosition(new Vector3f(-20f, 0, -20));
        zebra.setScale(new Vector3f(30, 30, 30));

        EntityGeneric wolf = new EntityGeneric(() -> new EffigyWolf());
        wolf.setPosition(new Vector3f(-140f, 0, -40));
        wolf.setScale(new Vector3f(30, 30, 30));

        EntityTransformable hagrid = new EntityGenericRotating(() -> new EffigyHagrid(), new Vector3f(0, 0.01f, 0));
        hagrid.setPosition(new Vector3f(20f, 2, 2));
        hagrid.setScale(new Vector3f(15, 15, 15));

        EntityGeneric spider = new EntityGeneric(() -> new EffigySpider());
        spider.setPosition(new Vector3f(20f, 2, -20));
        spider.setScale(new Vector3f(5, 5, 5));

        EntityGeneric warrior = new EntityGeneric(() -> new EffigyWarrior());
        warrior.setPosition(new Vector3f(-20f, 2, 2));
        warrior.setScale(new Vector3f(10, 10, 10));

        EntityGeneric dude = new EntityGeneric(() -> new EffigyDude());
        dude.setPosition(new Vector3f(-20f, 0, -20));
        dude.setScale(new Vector3f(10f, 10f, 10f));

        new EntityGenericRotating(() -> new Effigy3DText(new File("fonts/default.png"), "Hello world!"), new Vector3f(0, 0.1f, 0));
        new EntityGeneric(() -> new EffigyAxis());

        new EntityStatistics();

        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                EntityTransformable hagrid1 = new EntityGenericRotating(() -> new EffigyHagrid(), new Vector3f(0, 0.01f, 0));
                hagrid1.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                hagrid1.setScale(new Vector3f(15, 15, 15));
            }
        }
    }

    private void generateChunks() {
        for (int cx = 0; cx < 1; ++cx) {
            for (int cy = 0; cy < 1; ++cy) {
                for (int cz = 0; cz < 1; ++cz) {
                    int finalCx = cx;
                    int finalCy = cy;
                    int finalCz = cz;
                    new EntityGeneric(() -> new EffigyChunk(getChunks().getChunk(new Vector3i(finalCx, finalCy, finalCz))));
                }
            }
        }
    }

    private void update() {
        getEventSnapReceiver().update();
        getEntityRepository().getEntities().forEach(Entity::update);
    }

}
