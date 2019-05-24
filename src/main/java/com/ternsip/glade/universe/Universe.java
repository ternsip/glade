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
    private EntityFps entityFps;

    public void initialize() {

        sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));
        entityPlayer = new EntityPlayer();
        entityPlayer.setScale(new Vector3f(5, 5, 5));
        camera = new Camera(entityPlayer);

        Entity entityCube = new EntityCube();

        Entity entityLamp = new EntityLamp();
        entityLamp.setPosition(new Vector3f(-60f, 0, -60));
        entityLamp.setScale(new Vector3f(40, 40, 40));

        Entity entityBottle = new EntityBottle();
        entityBottle.setPosition(new Vector3f(-30f, 0, -20));
        entityBottle.setScale(new Vector3f(5, 5, 5));

        Entity entityZebra = new EntityZebra();
        entityZebra.setPosition(new Vector3f(-20f, 0, -20));
        entityZebra.setScale(new Vector3f(30, 30, 30));

        Entity entityWolf = new EntityWolf();
        entityWolf.setPosition(new Vector3f(-140f, 0, -40));
        entityWolf.setScale(new Vector3f(30, 30, 30));

        Entity entityHagrid = new EntityHagrid();
        entityHagrid.setPosition(new Vector3f(20f, 2, 2));
        entityHagrid.setScale(new Vector3f(15, 15, 15));

        Entity entitySpider = new EntitySpider();
        entitySpider.setPosition(new Vector3f(20f, 2, -20));
        entitySpider.setScale(new Vector3f(5, 5, 5));

        Entity entityWarrior = new EntityWarrior();
        entityWarrior.setPosition(new Vector3f(-20f, 2, 2));
        entityWarrior.setScale(new Vector3f(10, 10, 10));

        Entity entityDude2 = new EntityDude();
        entityDude2.setPosition(new Vector3f(-20f, 0, -20));
        entityDude2.setScale(new Vector3f(10f, 10f, 10f));

        Entity3DText entity3DText = new Entity3DText(new File("fonts/default.png"), "Hello world!", new Vector3f(0, 0.1f, 0));
        EntityAxis entityAxis = new EntityAxis();

        //EntityText entityText = new EntityText(new File("fonts/default.png"), "1234567890.1234567890");

        entityFps = new EntityFps(100);

        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                Entity entity = new EntityHagrid();
                entity.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                entity.setScale(new Vector3f(15, 15, 15));
            }
        }

    }

    public void update() {
        getEntityFps().update();
        getEntityRepository().update();
        getCamera().update();
        getSun().update();
    }

    public void finish() {
        getEntityRepository().finish();
    }
}
