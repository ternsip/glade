package com.ternsip.glade.model.loader.animation.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.Mesh;
import com.ternsip.glade.model.loader.engine.utils.OpenGlUtils;
import com.ternsip.glade.universal.AnimGameItem;

import java.util.List;

public class AnimatedModelRenderer {

    private AnimatedModelShader shader;

    public AnimatedModelRenderer() {
        this.shader = new AnimatedModelShader();
    }

    public void render(List<AnimGameItem> animGameItems, Camera camera, Sun sun) {
        for (AnimGameItem animGameItem : animGameItems) {
            render(animGameItem, camera, sun);
        }
    }

    public void render(AnimGameItem animGameItem, Camera camera, Sun sun) {
        shader.start();
        shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
        shader.lightDirection.loadVec3(sun.getPosition().normalize().negate());
        shader.jointTransforms.loadMatrixArray(animGameItem.getAnimator().getJointTransforms()); // TODO ANALOG
        shader.transformationMatrix.loadMatrix(animGameItem.getTransformationMatrix()); // TODO ANALOG
        OpenGlUtils.antialias(true);
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
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
