package com.ternsip.glade.model.loader.animation.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.loader.engine.utils.OpenGlUtils;
import org.lwjgl.opengl.GL11;

public class AnimatedModelRenderer {

    private AnimatedModelShader shader;

    public AnimatedModelRenderer() {
        this.shader = new AnimatedModelShader();
    }

    public void render(AnimatedModel animatedModel, Camera camera, Sun sun) {
        shader.start();
        shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
        shader.lightDirection.loadVec3(sun.getPosition().normalize().negate());
        OpenGlUtils.antialias(true);
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
        animatedModel.getTexture().bindToUnit(0);
        animatedModel.getModel().bind(0, 1, 2, 3, 4);
        shader.jointTransforms.loadMatrixArray(animatedModel.getJointTransforms());
        GL11.glDrawElements(GL11.GL_TRIANGLES, animatedModel.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
        animatedModel.getModel().unbind(0, 1, 2, 3, 4);
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

}
