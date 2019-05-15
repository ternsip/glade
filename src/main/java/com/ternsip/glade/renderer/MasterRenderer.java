package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Cube;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.sky.SkyRenderer;
import com.ternsip.glade.universal.*;
import org.joml.Vector3f;

import java.io.File;
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
        // TODO cleanup entities
    }

    public void prepareTestScene() {
        Model cubeModel = new Model(Cube.generateMesh());
        Entity entityCube = new Entity(cubeModel);

        Material[] lampMaterials = new Material[]{
                new Material()
                        .withTexture(new Texture(new File("models/lamp/color.png")))
                        .withDiffuseMap(new Texture(new File("models/lamp/Diffuse.png")))
                        .withAmbientMap(new Texture(new File("models/lamp/ambient occlusion.png")))
                        .withEmissiveMap(new Texture(new File("models/lamp/emissive.jpg")))
                        .withSpecularMap(new Texture(new File("models/lamp/Specular.png")))
                        .withNormalsMap(new Texture(new File("models/lamp/normal.png")))
        };
        Model lampModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/lamp/crystal_lamp_ring.fbx")).manualMeshMaterials(lampMaterials).build());
        Entity entityLamp = new Entity(lampModel);
        entityLamp.setPosition(new Vector3f(-60f, 0, -60));
        entityLamp.setScale(new Vector3f(0.05f, 0.05f, 0.05f));
        entityLamp.setRotation(new Vector3f(0, 0, 0));

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

        Material[] spiderMaterials = new Material[]{new Material().withDiffuseMap(new Texture(new File("models/spider/Spinnen_Bein_tex_2.jpg")))};
        Model spiderModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/spider/spider.dae")).manualMeshMaterials(spiderMaterials).build());
        Entity entitySpider = new Entity(spiderModel);
        entitySpider.setPosition(new Vector3f(20f, 2, -20));
        entitySpider.setScale(new Vector3f(1, 1, 1));
        entitySpider.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        Model warriorModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/warrior/warrior.3ds")).build());
        Entity entityWarrior = new Entity(warriorModel);
        entityWarrior.setPosition(new Vector3f(-20f, 2, 2));
        entityWarrior.setScale(new Vector3f(10, 10, 10));
        entityWarrior.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        Model dude2Model = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/dude/dude.3ds")).manualMeshMaterials((new Material[]{new Material(new Texture(new File("models/dude/dude.png")))})).build());
        Entity entityDude2 = new Entity(dude2Model);
        entityDude2.setPosition(new Vector3f(-20f, 0, -20));
        entityDude2.setScale(new Vector3f(10f, 10f, 10f));
        entityDude2.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));

        processEntity(entityCube);
        processEntity(entityLamp);
        processEntity(entityDude2);
        processEntity(entityZebra);
        processEntity(entityBottle);
        processEntity(entitySpider);
        processEntity(entityHagreed);
        processEntity(entityWarrior);

        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                Entity entity = new Entity(hagreedModel);
                entity.setPosition(new Vector3f(20f + 10 * i, 2, 2 + 10 * j));
                entity.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
                entity.setRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)));
                processEntity(entity);
            }
        }
    }


}
