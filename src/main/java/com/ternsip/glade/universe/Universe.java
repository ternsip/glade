package com.ternsip.glade.universe;

import com.ternsip.glade.graphics.display.DisplaySnapReceiver;
import com.ternsip.glade.universe.common.Sun;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.EntityGraphical;
import com.ternsip.glade.universe.entities.impl.EntityFps;
import com.ternsip.glade.universe.entities.impl.EntityGeneric;
import com.ternsip.glade.universe.entities.impl.EntityGenericRotating;
import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.repository.EntityRepository;
import com.ternsip.glade.universe.graphicals.impl.*;
import lombok.Getter;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@Getter
public class Universe {

    private final DisplaySnapReceiver displaySnapReceiver = new DisplaySnapReceiver();
    private Sun sun; // TODO SUN SHOULD BE THREAD SAFE (AFTER LIGHT)
    private EntityPlayer entityPlayer;
    private EntityRepository entityRepository = new EntityRepository();
    private int ticksPerSecond = 128;
    private final ReentrantLock lock = new ReentrantLock();

    public void initialize() {

        sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));
        entityPlayer = new EntityPlayer();
        entityPlayer.setScale(new Vector3f(5, 5, 5));
        DISPLAY_MANAGER.getCamera().setTarget(() -> entityPlayer.getPosition());

        spawnTestEntities();

    }

    @SneakyThrows
    public void loop() {
        while (getDisplaySnapReceiver().isApplicationActive()) {
            long startTime = System.currentTimeMillis();
            update();
            long pastTime = System.currentTimeMillis() - startTime;
            long needToSleep = (long) Math.max(1000.0f / ticksPerSecond - pastTime, 0);
            if (needToSleep > 0) {
                Thread.sleep(needToSleep);
            }
        }
    }

    private void update() {
        try {
            getLock().lock();
            getDisplaySnapReceiver().update();
            getSun().update();
            getEntityRepository().getEntities().forEach(Entity::update);
        } finally {
            getLock().unlock();
        }
    }

    private void spawnTestEntities() {
        EntityGeneric cube = new EntityGeneric(e -> new GraphicalCube());

        EntityGeneric lamp = new EntityGeneric(e -> new GraphicalLamp());
        lamp.setPosition(new Vector3f(-60f, 0, -60));
        lamp.setScale(new Vector3f(40, 40, 40));

        EntityGeneric bottle = new EntityGeneric(e -> new GraphicalBottle());
        bottle.setPosition(new Vector3f(-30f, 0, -20));
        bottle.setScale(new Vector3f(5, 5, 5));

        EntityGeneric zebra = new EntityGeneric(e -> new GraphicalZebra());
        zebra.setPosition(new Vector3f(-20f, 0, -20));
        zebra.setScale(new Vector3f(30, 30, 30));

        EntityGeneric wolf = new EntityGeneric(e -> new GraphicalWolf());
        wolf.setPosition(new Vector3f(-140f, 0, -40));
        wolf.setScale(new Vector3f(30, 30, 30));

        EntityGraphical hagrid = new EntityGenericRotating(e -> new GraphicalHagrid(), new Vector3f(0, 0.01f, 0));
        hagrid.setPosition(new Vector3f(20f, 2, 2));
        hagrid.setScale(new Vector3f(15, 15, 15));

        EntityGeneric spider = new EntityGeneric(e -> new GraphicalSpider());
        spider.setPosition(new Vector3f(20f, 2, -20));
        spider.setScale(new Vector3f(5, 5, 5));

        EntityGeneric warrior = new EntityGeneric(e -> new GraphicalWarrior());
        warrior.setPosition(new Vector3f(-20f, 2, 2));
        warrior.setScale(new Vector3f(10, 10, 10));

        EntityGeneric dude = new EntityGeneric(e -> new GraphicalDude());
        dude.setPosition(new Vector3f(-20f, 0, -20));
        dude.setScale(new Vector3f(10f, 10f, 10f));

        new EntityGenericRotating(e -> new Graphical3DText(new File("fonts/default.png"), "Hello world!"), new Vector3f(0, 0.1f, 0));
        new EntityGeneric(e -> new GraphicalAxis());
        new EntityGeneric(e -> new GraphicalSky());

        new EntityFps(100);

        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                EntityGraphical hagrid1 = new EntityGenericRotating(e -> new GraphicalHagrid(), new Vector3f(0, 0.01f, 0));
                hagrid1.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                hagrid1.setScale(new Vector3f(15, 15, 15));
            }
        }
    }

}
