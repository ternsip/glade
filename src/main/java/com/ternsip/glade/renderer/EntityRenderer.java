package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Entity;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.RawModel;
import com.ternsip.glade.model.TexturedModel;
import com.ternsip.glade.shader.model.ModelShader;
import com.ternsip.glade.texture.ModelTexture;
import com.ternsip.glade.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

import static com.ternsip.glade.sky.SkyRenderer.SKY_COLOR;


public class EntityRenderer {

    private ModelShader modelShader;

    public EntityRenderer(Matrix4f projectionMatrix) {
        this.modelShader = new ModelShader();
        modelShader.start();
        modelShader.loadProjectionMatrix(projectionMatrix);
        modelShader.stop();
    }

    public void render(Map<TexturedModel, List<Entity>> entities, Camera camera, Sun sun) {
        modelShader.start();
        modelShader.loadSkyColour(SKY_COLOR);
        modelShader.loadLight(sun);
        modelShader.loadViewMatrix(camera);
        for (TexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                        GL11.GL_UNSIGNED_SHORT, 0);
            }
            unbindTexturedModel();
        }
        modelShader.stop();
    }

    public void cleanUp() {
        modelShader.cleanUp();
    }

    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        ModelTexture texture = model.getTexture();

        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }

        modelShader.loadFakeLightingVariable(texture.isUseFakeLighting());

        modelShader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        modelShader.loadTransformationMatrix(entity.getTransformationMatrix());
    }

    public Quaternionfc addRotation(float rx, float ry, float rz) {
        Quaternionf rotation = new Quaternionf();
        // Rotate Y axis
        new Quaternionf(1f, 0f, 0f, ry * Maths.PI_OVER_180).mul(rotation, rotation);
        rotation.normalize();
        // Rotate X axis
        rotation.mul(new Quaternionf(0f, 1f, 0f, rx * Maths.PI_OVER_180), rotation);
        rotation.normalize();
        // Rotate Z axis
        new Quaternionf(0f, 0f, 1f, rz * Maths.PI_OVER_180).mul(rotation, rotation);
        rotation.normalize();

        return rotation;
    }

}
