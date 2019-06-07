package com.ternsip.glade.universe;

import com.ternsip.glade.universe.common.Camera;
import com.ternsip.glade.universe.common.Sun;
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

@Getter
public class Universe {

    private Sun sun;
    private EntityPlayer entityPlayer;
    private Camera camera;
    private EntityRepository entityRepository = new EntityRepository();
    private int ticksPerSecond = 128;

    public void initialize() {

        sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));
        entityPlayer = new EntityPlayer();
        entityPlayer.setScale(new Vector3f(5, 5, 5));
        camera = new Camera(entityPlayer);

        EntityGeneric cube = new EntityGeneric(new GraphicalCube());

        EntityGeneric lamp = new EntityGeneric(new GraphicalLamp());
        lamp.setPosition(new Vector3f(-60f, 0, -60));
        lamp.setScale(new Vector3f(40, 40, 40));

        EntityGeneric bottle = new EntityGeneric(new GraphicalBottle());
        bottle.setPosition(new Vector3f(-30f, 0, -20));
        bottle.setScale(new Vector3f(5, 5, 5));

        EntityGeneric zebra = new EntityGeneric(new GraphicalZebra());
        zebra.setPosition(new Vector3f(-20f, 0, -20));
        zebra.setScale(new Vector3f(30, 30, 30));

        EntityGeneric wolf = new EntityGeneric(new GraphicalWolf());
        wolf.setPosition(new Vector3f(-140f, 0, -40));
        wolf.setScale(new Vector3f(30, 30, 30));

        EntityGraphical hagrid = new EntityGenericRotating(new GraphicalHagrid(), new Vector3f(0, 0.01f, 0));
        hagrid.setPosition(new Vector3f(20f, 2, 2));
        hagrid.setScale(new Vector3f(15, 15, 15));

        EntityGeneric spider = new EntityGeneric(new GraphicalSpider());
        spider.setPosition(new Vector3f(20f, 2, -20));
        spider.setScale(new Vector3f(5, 5, 5));

        EntityGeneric warrior = new EntityGeneric(new GraphicalWarrior());
        warrior.setPosition(new Vector3f(-20f, 2, 2));
        warrior.setScale(new Vector3f(10, 10, 10));

        EntityGeneric dude = new EntityGeneric(new GraphicalDude());
        dude.setPosition(new Vector3f(-20f, 0, -20));
        dude.setScale(new Vector3f(10f, 10f, 10f));

        new EntityGenericRotating(new Graphical3DText(new File("fonts/default.png"), "Hello world!"), new Vector3f(0, 0.1f, 0));
        new EntityGeneric(new GraphicalAxis());
        new EntityGeneric(new GraphicalSky());

        new EntityFps(100);

        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                EntityGraphical hagrid1 = new EntityGenericRotating(new GraphicalHagrid(), new Vector3f(0, 0.01f, 0));
                hagrid1.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                hagrid1.setScale(new Vector3f(15, 15, 15));
            }
        }

    }

    @SneakyThrows
    public void update() {
        long startTime = System.currentTimeMillis();
        getCamera().update();
        getSun().update();
        long pastTime = System.currentTimeMillis() - startTime;
        long needToSleep = (long) Math.max(1000.0f / ticksPerSecond - pastTime, 0);
        if (needToSleep > 0) {
            Thread.sleep(needToSleep);
        }
    }

}
