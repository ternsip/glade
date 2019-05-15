package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.sky.SkyRenderer;
import com.ternsip.glade.universal.Entity;
import com.ternsip.glade.universal.entities.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.sky.SkyRenderer.SKY_COLOR;
import static org.lwjgl.opengl.GL11.*;

public class MasterRenderer {

    private List<Entity> entities = new ArrayList<>();

    private SkyRenderer skyRenderer;
    private AnimatedModelRenderer animatedModelRenderer;

    public MasterRenderer(Camera camera) {
        enableCulling();
        Camera.createProjectionMatrix();
        skyRenderer = new SkyRenderer(camera.getProjectionMatrix());
        animatedModelRenderer = new AnimatedModelRenderer();
    }

    public static void enableCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glViewport(0, 0, DISPLAY_MANAGER.getWidth(), DISPLAY_MANAGER.getHeight());
    }

    public static void disableCulling() {
        glDisable(GL_CULL_FACE);
    }

    public void render(Sun sun, Camera camera) {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(SKY_COLOR.x(), SKY_COLOR.y(), SKY_COLOR.z(), 1);
        skyRenderer.render(sun, camera);
        animatedModelRenderer.render(entities, camera, sun);
    }

    public void processEntity(Entity entity) {
        entities.add(entity);
    }

    public void cleanUp() {
        skyRenderer.cleanUp();
        animatedModelRenderer.cleanUp();
    }

    public void prepareTestScene() {

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

        processEntity(entityCube);
        processEntity(entityLamp);
        processEntity(entityDude2);
        processEntity(entityZebra);
        processEntity(entityBottle);
        processEntity(entitySpider);
        processEntity(entityHagrid);
        processEntity(entityWarrior);

        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                Entity entity = new EntityHagrid();
                entity.setPosition(new Vector3f(20f + 10 * i, 2, 2 + 10 * j));
                entity.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
                entity.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));
                processEntity(entity);
            }
        }
    }


}
