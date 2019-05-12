package com.ternsip.glade.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
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

    public void render(List<Model> animGameItems, Camera camera, Sun sun) {
        for (Model animGameItem : animGameItems) {
            render(animGameItem, camera, sun);
        }
    }

    public void render(Model animGameItem, Camera camera, Sun sun) {
        shader.start();
        Matrix4f[] boneTransforms = animGameItem.getAnimator().getBoneTransforms();
        //boneTransforms = new Matrix4f[0];
        shader.animated.loadBoolean(boneTransforms.length > 0);
        shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
        shader.lightDirection.loadVec3(sun.getPosition().normalize().negate());
        shader.boneTransforms.loadMatrixArray(boneTransforms); // TODO ANALOG
        shader.transformationMatrix.loadMatrix(animGameItem.getTransformationMatrix());
        OpenGlUtils.antialias(true);
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
        //animGameItem.getMesh().render();
        for (Mesh mesh : animGameItem.getMeshes()) {
            mesh.render();
        }
        shader.stop();
        animGameItem.getAnimator().update();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

}
