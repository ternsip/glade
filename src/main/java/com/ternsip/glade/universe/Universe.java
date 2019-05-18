package com.ternsip.glade.universe;

import com.ternsip.glade.universe.common.Camera;
import com.ternsip.glade.universe.common.Sun;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.EntityRepository;
import com.ternsip.glade.universe.entities.impl.*;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

@Getter
public class Universe {

    private EntityRepository entityRepository = new EntityRepository();

    private Sun sun;
    private EntityPlayer entityPlayer;
    private Camera camera;

    public void initialize() {

        sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));
        entityPlayer = new EntityPlayer();
        entityPlayer.setScale(new Vector3f(5, 5, 5));
        camera = new Camera(entityPlayer);

        Entity entityCube = new EntityCube();

        Entity entityLamp = new EntityLamp();
        entityLamp.setPosition(new Vector3f(-60f, 0, -60));
        entityLamp.setScale(new Vector3f(40, 40, 40));
        entityLamp.setRotation(new Vector3f(0, 0, 0));

        Entity entityBottle = new EntityBottle();
        entityBottle.setPosition(new Vector3f(-30f, 0, -20));
        entityBottle.setScale(new Vector3f(5, 5, 5));
        entityBottle.setRotation(new Vector3f(0, 0, 0));

        Entity entityZebra = new EntityZebra();
        entityZebra.setPosition(new Vector3f(-20f, 0, -20));
        entityZebra.setScale(new Vector3f(30, 30, 30));
        entityZebra.setRotation(new Vector3f(0, 0, 0));

        Entity entityHagrid = new EntityHagrid();
        entityHagrid.setPosition(new Vector3f(20f, 2, 2));
        entityHagrid.setScale(new Vector3f(15, 15, 15));
        entityHagrid.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2))); // TODO BUG IF I PUT 180 ROTATION

        Entity entitySpider = new EntitySpider();
        entitySpider.setPosition(new Vector3f(20f, 2, -20));
        entitySpider.setScale(new Vector3f(5, 5, 5));
        entitySpider.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        Entity entityWarrior = new EntityWarrior();
        entityWarrior.setPosition(new Vector3f(-20f, 2, 2));
        entityWarrior.setScale(new Vector3f(10, 10, 10));
        entityWarrior.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        Entity entityDude2 = new EntityDude();
        entityDude2.setPosition(new Vector3f(-20f, 0, -20));
        entityDude2.setScale(new Vector3f(10f, 10f, 10f));
        entityDude2.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        String s = "";
        for (char c = 0; c < 256; ++c) {
            s += c;
        }
        EntityText entityText = new EntityText(new File("fonts/default.png"), s);
        entityText.setScale(new Vector3f(1, 1, 1));
        entityText.setRotation(new Vector3f(0, (float) (-Math.PI - 0.01), 0));

        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                Entity entity = new EntityHagrid();
                entity.setPosition(new Vector3f(20f + 10 * i, 2, 2 + 10 * j));
                entity.setScale(new Vector3f(15, 15, 15));
                entity.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));
            }
        }

    }

    public void update() {
        entityPlayer.move();
        camera.move();
        sun.move();
    }

    public void finish() {
        entityRepository.finish();
    }
}
