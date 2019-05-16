package com.ternsip.glade.universe;

import com.ternsip.glade.universe.common.Camera;
import com.ternsip.glade.universe.common.Sun;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.*;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

import static com.ternsip.glade.Glade.UNIVERSE;

@Getter
public class Universe {

    private Set<Entity> entities = new HashSet<>();

    private Sun sun;
    private EntityPlayer entityPlayer;
    private Camera camera;

    public void initialize() {
        sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));
        entityPlayer = new EntityPlayer();
        camera = new Camera(entityPlayer);

        Entity entityCube = new EntityCube();

        Entity entityLamp = new EntityLamp();
        entityLamp.setPosition(new Vector3f(-60f, 0, -60));
        entityLamp.setScale(new Vector3f(0.05f, 0.05f, 0.05f));
        entityLamp.setRotation(new Vector3f(0, 0, 0));

        Entity entityBottle = new EntityBottle();
        entityBottle.setPosition(new Vector3f(-30f, 0, -20));
        entityBottle.setScale(new Vector3f(1f, 1f, 1f));
        entityBottle.setRotation(new Vector3f(0, 0, 0));

        Entity entityZebra = new EntityZebra();
        entityZebra.setPosition(new Vector3f(-20f, 0, -20));
        entityZebra.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        entityZebra.setRotation(new Vector3f(0, 0, 0));

        Entity entityHagrid = new EntityHagrid();
        entityHagrid.setPosition(new Vector3f(20f, 2, 2));
        entityHagrid.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        entityHagrid.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2))); // TODO BUG IF I PUT 180 ROTATION

        Entity entitySpider = new EntitySpider();
        entitySpider.setPosition(new Vector3f(20f, 2, -20));
        entitySpider.setScale(new Vector3f(1, 1, 1));
        entitySpider.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        Entity entityWarrior = new EntityWarrior();
        entityWarrior.setPosition(new Vector3f(-20f, 2, 2));
        entityWarrior.setScale(new Vector3f(10, 10, 10));
        entityWarrior.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        Entity entityDude2 = new EntityDude();
        entityDude2.setPosition(new Vector3f(-20f, 0, -20));
        entityDude2.setScale(new Vector3f(10f, 10f, 10f));
        entityDude2.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        EntityText entityText = new EntityText();
        entityText.setScale(new Vector3f(10f, 10, 10));
        entityText.setRotation(new Vector3f(0, (float) (-Math.PI - 0.01), 0));

        UNIVERSE.getEntities().add(entityText);
        UNIVERSE.getEntities().add(entityPlayer);
        UNIVERSE.getEntities().add(entityCube);
        UNIVERSE.getEntities().add(entityLamp);
        UNIVERSE.getEntities().add(entityDude2);
        UNIVERSE.getEntities().add(entityZebra);
        UNIVERSE.getEntities().add(entityBottle);
        UNIVERSE.getEntities().add(entitySpider);
        UNIVERSE.getEntities().add(entityHagrid);
        UNIVERSE.getEntities().add(entityWarrior);

        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                Entity entity = new EntityHagrid();
                entity.setPosition(new Vector3f(20f + 10 * i, 2, 2 + 10 * j));
                entity.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
                entity.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));
                UNIVERSE.getEntities().add(entity);
            }
        }

    }

    public void update() {
        entityPlayer.move();
        camera.move();
        sun.move();
    }

    public void finish() {

    }
}
