package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Entity;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.shader.model.ModelShader;
import org.joml.Matrix4f;

import java.util.List;

import static com.ternsip.glade.sky.SkyRenderer.SKY_COLOR;


public class EntityRenderer {

    private ModelShader modelShader;

    public EntityRenderer(Matrix4f projectionMatrix) {
        this.modelShader = new ModelShader();
        modelShader.start();
        modelShader.loadProjectionMatrix(projectionMatrix);
        modelShader.stop();
    }

    public void render(List<Entity> entities, Camera camera, Sun sun) {
        modelShader.start();
        modelShader.loadSkyColour(SKY_COLOR);
        modelShader.loadLight(sun);
        modelShader.loadViewMatrix(camera);
        for (Entity entity : entities) {
            modelShader.loadTransformationMatrix(entity.getTransformationMatrix());
            if (false) {
                MasterRenderer.disableCulling();
            }
            modelShader.loadFakeLightingVariable(false);
            modelShader.loadShineVariables(1, 0);
            entity.getModel().render();
            MasterRenderer.enableCulling();
        }
        modelShader.stop();
    }

    public void cleanUp() {
        modelShader.cleanUp();
    }

}
