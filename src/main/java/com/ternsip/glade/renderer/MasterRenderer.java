package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Cube;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.sky.SkyRenderer;
import com.ternsip.glade.universal.AssimpLoader;
import com.ternsip.glade.universal.Entity;
import com.ternsip.glade.universal.Model;
import com.ternsip.glade.universal.Settings;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.sky.SkyRenderer.SKY_COLOR;

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
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glViewport(0, 0, DISPLAY_MANAGER.getWidth(), DISPLAY_MANAGER.getHeight());
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(Sun sun, Camera camera) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(SKY_COLOR.x(), SKY_COLOR.y(), SKY_COLOR.z(), 1);
        skyRenderer.render(sun, camera);
        animatedModelRenderer.render(entities, camera, sun);
    }

    public void processEntity(Entity entity) {
        entities.add(entity);
    }

    public void cleanUp() {
        skyRenderer.cleanUp();
        animatedModelRenderer.cleanUp();
        // TODO cleanup entities
    }

    public void prepareTestScene() {
        Model cubeModel = new Model(Cube.generateMesh());
        Entity entityCube = new Entity(cubeModel);

        Model bottleModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/bottle/bottle.3ds")).build());
        Entity entityBottle = new Entity(bottleModel);
        entityBottle.setPosition(new Vector3f(-30f, 0, -20));
        entityBottle.setScale(new Vector3f(1f, 1f, 1f));
        entityBottle.setRotation(new Vector3f(0, 0, 0));

        Model zebraModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/zebra/ZebraLOD1.ms3d")).build());
        Entity entityZebra = new Entity(zebraModel);
        entityZebra.setPosition(new Vector3f(-20f, 0, -20));
        entityZebra.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        entityZebra.setRotation(new Vector3f(0, 0, 0));

        Model hagreedModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/bob/boblamp.md5mesh")).animationFile(new File("models/bob/boblamp.md5anim")).build());
        Entity entityHagreed = new Entity(hagreedModel);
        entityHagreed.setPosition(new Vector3f(20f, 2, 2));
        entityHagreed.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        entityHagreed.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2))); // TODO BUG IF I PUT 180 ROTATION

        Model spiderModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/spider/spider.dae")).manualTexture(new File("models/spider/Spinnen_Bein_tex_2.jpg")).build());
        Entity entitySpider = new Entity(spiderModel);
        entitySpider.setPosition(new Vector3f(20f, 2, -20));
        entitySpider.setScale(new Vector3f(1, 1, 1));
        entitySpider.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        Model warriorModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/warrior/warrior.3ds")).build());
        Entity entityWarrior = new Entity(warriorModel);
        entityWarrior.setPosition(new Vector3f(-20f, 2, 2));
        entityWarrior.setScale(new Vector3f(10, 10, 10));
        entityWarrior.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));


        Model dude2Model = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/dude/dude.3ds")).build());
        Entity entityDude2 = new Entity(dude2Model);
        entityDude2.setPosition(new Vector3f(-20f, 0, -20));
        entityDude2.setScale(new Vector3f(10f, 10f, 10f));
        entityDude2.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        processEntity(entityCube);
        processEntity(entityDude2);
        processEntity(entityZebra);
        processEntity(entityBottle);
        processEntity(entitySpider);
        processEntity(entityHagreed);
        processEntity(entityWarrior);
    }


}
