package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.universal.Entity;
import com.ternsip.glade.universal.Mesh;
import com.ternsip.glade.universal.Model;
import com.ternsip.glade.utils.OpenGlUtils;
import org.joml.Matrix4f;

import java.util.List;

public class AnimatedModelRenderer {

    private AnimatedModelShader shader;

    public AnimatedModelRenderer() {
        this.shader = new AnimatedModelShader();
    }

    public void render(List<Entity> animGameItems, Camera camera, Sun sun) {
        for (Entity entity : animGameItems) {
            render(entity, camera, sun);
        }
    }

    public void render(Entity entity, Camera camera, Sun sun) {
        shader.start();
        Matrix4f[] boneTransforms = entity.getAnimator().getBoneTransforms();
        shader.animated.loadBoolean(boneTransforms.length > 0);
        shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
        shader.lightDirection.loadVec3(sun.getPosition().normalize().negate());
        shader.boneTransforms.loadMatrixArray(boneTransforms); // TODO ANALOG
        shader.transformationMatrix.loadMatrix(entity.getTransformationMatrix());
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
