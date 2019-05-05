package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Entity;
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


public class EntityRenderer {

    private ModelShader shader;

    public EntityRenderer(ModelShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<TexturedModel, List<Entity>> entities) {
        for (TexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();

        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }

        shader.loadFakeLightingVariable(texture.isUseFakeLighting());

        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {

//		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
//				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                Entity.getRotationQuat(entity.getRotX(), entity.getRotY(), entity.getRotZ()), entity.getScale());

        shader.loadTransformationMatrix(transformationMatrix);
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
