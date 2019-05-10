package com.ternsip.glade.model.loader.engine.render;

import com.ternsip.glade.model.loader.animation.renderer.AnimatedModelRenderer;
import com.ternsip.glade.model.loader.engine.scene.Scene;

/**
 * This class represents the entire render engine.
 * 
 * @author Karl
 *
 */
public class RenderEngine {

	private MasterRenderer renderer;

	private RenderEngine(MasterRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Renders the scene to the screen.
	 * 
	 * @param scene
	 *            - the game scene.
	 */
	public void renderScene(Scene scene) {
		renderer.renderScene(scene);
	}

	/**
	 * Cleans up the renderers and closes the display.
	 */
	public void close() {
		renderer.cleanUp();
	}

	/**
	 * Initializes a new render engine. Creates the display and inits the
	 * renderers.
	 * 
	 * @return
	 */
	public static RenderEngine init() {
		AnimatedModelRenderer entityRenderer = new AnimatedModelRenderer();
		MasterRenderer renderer = new MasterRenderer(entityRenderer);
		return new RenderEngine(renderer);
	}

}
