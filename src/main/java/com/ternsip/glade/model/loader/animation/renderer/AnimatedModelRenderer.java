package com.ternsip.glade.model.loader.animation.renderer;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.loader.engine.utils.OpenGlUtils;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class AnimatedModelRenderer {

	private AnimatedModelShader shader;

	public AnimatedModelRenderer() {
		this.shader = new AnimatedModelShader();
	}

	public void render(AnimatedModel entity, Camera camera, Vector3f lightDir) {
		shader.start();
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		shader.lightDirection.loadVec3(lightDir);
		OpenGlUtils.antialias(true);
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
		entity.getTexture().bindToUnit(0);
		entity.getModel().bind(0, 1, 2, 3, 4);
		shader.jointTransforms.loadMatrixArray(entity.getJointTransforms());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		entity.getModel().unbind(0, 1, 2, 3, 4);
		shader.stop();
	}

	public void cleanUp() {
		shader.cleanUp();
	}

}
