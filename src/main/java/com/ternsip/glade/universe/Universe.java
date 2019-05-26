package com.ternsip.glade.universe;

import com.ternsip.glade.graphics.display.DisplaySnapReceiver;
import com.ternsip.glade.graphics.common.Camera;
import com.ternsip.glade.graphics.entities.impl.FigureSky;
import com.ternsip.glade.graphics.entities.base.Entity;
import com.ternsip.glade.graphics.entities.base.FigureRepository;
import com.ternsip.glade.graphics.entities.impl.*;
import com.ternsip.glade.universe.entities.EntityPlayer;
import com.ternsip.glade.utils.Timer;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public class Universe {

    public static Universe INSTANCE;

    private final DisplaySnapReceiver displaySnapReceiver = new DisplaySnapReceiver();
    private final FigureRepository figureRepository;
    private final FigureSky figureSky;
    private final EntityPlayer entityPlayer;
    private final Camera camera;
    private final EntityFps entityFps;
    private final Timer tickTimer;
    private final ReentrantLock lock = new ReentrantLock();

    public Universe() {
        INSTANCE = this;
        this.figureRepository = new FigureRepository();
        this.figureSky = new FigureSky(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));
        this.entityPlayer = new EntityPlayer();
        this.entityPlayer.setScale(new Vector3f(5, 5, 5));
        this.camera = new Camera(entityPlayer);
        this.entityFps = new EntityFps(100);
        this.tickTimer = new Timer(1000 / 128);

        generateTestEntities();

        loop();
        finish();
    }

    private void generateTestEntities() {
        Entity entityCube = new EntityCube();

        Entity entityLamp = new EntityLamp();
        entityLamp.setPosition(new Vector3f(-60f, 0, -60));
        entityLamp.setScale(new Vector3f(40, 40, 40));

        Entity entityBottle = new EntityBottle();
        entityBottle.setPosition(new Vector3f(-30f, 0, -20));
        entityBottle.setScale(new Vector3f(5, 5, 5));

        Entity entityZebra = new FigureZebra();
        entityZebra.setPosition(new Vector3f(-20f, 0, -20));
        entityZebra.setScale(new Vector3f(30, 30, 30));

        Entity entityWolf = new FigureWolf();
        entityWolf.setPosition(new Vector3f(-140f, 0, -40));
        entityWolf.setScale(new Vector3f(30, 30, 30));

        Entity entityHagrid = new EntityHagrid();
        entityHagrid.setPosition(new Vector3f(20f, 2, 2));
        entityHagrid.setScale(new Vector3f(15, 15, 15));

        Entity entitySpider = new FigureSpider();
        entitySpider.setPosition(new Vector3f(20f, 2, -20));
        entitySpider.setScale(new Vector3f(5, 5, 5));

        Entity entityWarrior = new FigureWarrior();
        entityWarrior.setPosition(new Vector3f(-20f, 2, 2));
        entityWarrior.setScale(new Vector3f(10, 10, 10));

        Entity entityDude2 = new EntityDude();
        entityDude2.setPosition(new Vector3f(-20f, 0, -20));
        entityDude2.setScale(new Vector3f(10f, 10f, 10f));

        Figure3DText figure3DText = new Figure3DText(new File("fonts/default.png"), "Hello world!", new Vector3f(0, 0.1f, 0));
        FigureAxis figureAxis = new FigureAxis();

        //FigureText entityText = new FigureText(new File("fonts/default.png"), "1234567890.1234567890");

        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                Entity entity = new EntityHagrid();
                entity.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                entity.setScale(new Vector3f(15, 15, 15));
            }
        }
    }

    private void loop() {
        while (displaySnapReceiver.isApplicationActive()) {
            getLock().lock();
            try {
                if (!getTickTimer().isOver()) {
                    return;
                }
                getTickTimer().drop();

                getEntityFps().update();
                getFigureRepository().update();
                getCamera().update();
                getFigureSky().update();

            } finally {
                getLock().unlock();
            }
        }
    }

    private void finish() {
        getFigureRepository().finish();
    }
}
