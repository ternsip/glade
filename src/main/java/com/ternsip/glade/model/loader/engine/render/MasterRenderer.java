package com.ternsip.glade.model.loader.engine.render;

import com.ternsip.glade.model.loader.animation.renderer.AnimatedModelRenderer;
import com.ternsip.glade.model.loader.engine.scene.Scene;
import org.lwjgl.opengl.GL11;

/**
 * This class is in charge of rendering everything in the scene to the screen.
 * @author Karl
 *
 */
public class MasterRenderer {

	private AnimatedModelRenderer entityRenderer;

	protected MasterRenderer(AnimatedModelRenderer renderer) {
		this.entityRenderer = renderer;
	}

	/**
	 * Renders the scene to the screen.
	 * @param scene
	 */
	protected void renderScene(Scene scene) {
		prepare();
		entityRenderer.render(scene.getAnimatedModel(), scene.getCamera(), scene.getLightDirection());
	}

	/**
	 * Clean up when the game is closed.
	 */
	protected void cleanUp() {
		entityRenderer.cleanUp();
	}

	/**
	 * Prepare to render the current frame by clearing the framebuffer.
	 */
	private void prepare() {
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}


}
