package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Entity;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.loader.animation.renderer.AnimatedModelRenderer;
import com.ternsip.glade.model.loader.engine.scene.Scene;
import com.ternsip.glade.sky.SkyRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.sky.SkyRenderer.SKY_COLOR;

public class MasterRenderer {

    private EntityRenderer entityRenderer;

    private List<Entity> entities = new ArrayList<>();
    private List<AnimatedModel> animatedModels = new ArrayList<>();

    private SkyRenderer skyRenderer;
    private AnimatedModelRenderer animatedModelRenderer;

    public MasterRenderer(Camera camera) {
        enableCulling();
        Camera.createProjectionMatrix();
        entityRenderer = new EntityRenderer(camera.getProjectionMatrix());
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
        entityRenderer.render(entities, camera, sun);
        skyRenderer.render(sun, camera);
        animatedModelRenderer.render(animatedModels, camera, sun);
    }

    public void processEntity(Entity entity) {
        entities.add(entity);
    }

    public void processEntity(AnimatedModel animatedModel) {
        animatedModels.add(animatedModel);
    }

    public void cleanUp() {
        entityRenderer.cleanUp();
        skyRenderer.cleanUp();
        animatedModelRenderer.cleanUp();
    }


}
