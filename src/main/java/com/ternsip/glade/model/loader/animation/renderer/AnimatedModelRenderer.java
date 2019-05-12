package com.ternsip.glade.model.loader.animation.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.Mesh;
import com.ternsip.glade.model.loader.engine.utils.OpenGlUtils;
import com.ternsip.glade.universal.Model;
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
        Matrix4f[] jointTransforms = animGameItem.getAnimator().getJointTransforms();
        //jointTransforms = new Matrix4f[0];
        shader.animated.loadBoolean(jointTransforms.length > 0);
        shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
        shader.lightDirection.loadVec3(sun.getPosition().normalize().negate());
        shader.jointTransforms.loadMatrixArray(jointTransforms); // TODO ANALOG
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
