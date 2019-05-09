package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Entity;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.sky.SkyRenderer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.sky.SkyRenderer.SKY_COLOR;

public class MasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;
    private EntityRenderer entityRenderer;

    private List<Entity> entities = new ArrayList<>();

    private SkyRenderer skyRenderer;

    public MasterRenderer() {
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(projectionMatrix);
        skyRenderer = new SkyRenderer(projectionMatrix);
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
    }

    public void processEntity(Entity entity) {
        entities.add(entity);
    }

    public void cleanUp() {
        entityRenderer.cleanUp();
        skyRenderer.cleanUp();
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) DISPLAY_MANAGER.getWidth() / (float) DISPLAY_MANAGER.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);
    }


}
