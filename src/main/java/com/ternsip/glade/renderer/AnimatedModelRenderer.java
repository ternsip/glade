package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.shader.base.ShaderProgram;
import com.ternsip.glade.shader.impl.AnimatedModelShader;
import com.ternsip.glade.universal.Entity;
import com.ternsip.glade.universal.Mesh;
import com.ternsip.glade.utils.OpenGlUtils;
import org.joml.Matrix4f;

import java.util.List;

public class AnimatedModelRenderer {

    private AnimatedModelShader shader;

    public AnimatedModelRenderer() {
        this.shader = ShaderProgram.createShader(AnimatedModelShader.class);
    }

    public void render(List<Entity> animGameItems, Camera camera, Sun sun) {
        for (Entity entity : animGameItems) {
            render(entity, camera, sun);
        }
    }

    public void render(Entity entity, Camera camera, Sun sun) {
        shader.start();
        Matrix4f[] boneTransforms = entity.getAnimator().getBoneTransforms();
        shader.getDiffuseMap().load(0);
        shader.getAnimated().load(boneTransforms.length > 0);
        shader.getProjectionMatrix().load(camera.getProjectionMatrix());
        shader.getViewMatrix().load(camera.createViewMatrix());
        shader.getLightDirection().load(sun.getPosition().normalize().negate());
        shader.getBoneTransforms().load(boneTransforms);
        shader.getTransformationMatrix().load(entity.getTransformationMatrix());
        OpenGlUtils.antialias(true);
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
        for (Mesh mesh : entity.getAnimator().getModel().getMeshes()) {
            mesh.render();
        }
        shader.stop();
        entity.getAnimator().update();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

}
