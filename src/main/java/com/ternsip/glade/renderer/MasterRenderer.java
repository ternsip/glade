package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.sky.SkyRenderer;
import com.ternsip.glade.universal.Model;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.sky.SkyRenderer.SKY_COLOR;

public class MasterRenderer {

    private List<Model> models = new ArrayList<>();

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
        animatedModelRenderer.render(models, camera, sun);
    }

    public void processEntity(Model animGameItem) {
        models.add(animGameItem);
    }

    public void cleanUp() {
        skyRenderer.cleanUp();
        animatedModelRenderer.cleanUp();
        // TODO cleanup entities
    }


}
