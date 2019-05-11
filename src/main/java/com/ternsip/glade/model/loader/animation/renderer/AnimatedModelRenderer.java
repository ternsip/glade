package com.ternsip.glade.model.loader.animation.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.loader.engine.utils.OpenGlUtils;
import com.ternsip.glade.universal.AnimGameItem;

import java.util.List;

public class AnimatedModelRenderer {

    private AnimatedModelShader shader;

    public AnimatedModelRenderer() {
        this.shader = new AnimatedModelShader();
    }

    public void render(List<AnimatedModel> animatedModels, List<AnimGameItem> animGameItems, Camera camera, Sun sun) {
        for (AnimatedModel animatedModel : animatedModels) {
            render(animatedModel, camera, sun);
        }
        for (AnimGameItem animGameItem : animGameItems) {
            render(animGameItem, camera, sun);
        }
    }

    public void render(AnimatedModel animatedModel, Camera camera, Sun sun) {
        shader.start();
        shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
        shader.lightDirection.loadVec3(sun.getPosition().normalize().negate());
        shader.jointTransforms.loadMatrixArray(animatedModel.getAnimator().getJointTransforms()); // TODO ANALOG
        OpenGlUtils.antialias(true);
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
        animatedModel.getModel().render();
        shader.stop();
        animatedModel.getAnimator().update();
    }

    public void render(AnimGameItem animGameItem, Camera camera, Sun sun) {
        shader.start();
        shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
        shader.lightDirection.loadVec3(sun.getPosition().normalize().negate());
        shader.jointTransforms.loadMatrixArray(animGameItem.getAnimator().getJointTransforms()); // TODO ANALOG
        OpenGlUtils.antialias(true);
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
        animGameItem.getMesh().render();
        shader.stop();
        animGameItem.getAnimator().update();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

}
