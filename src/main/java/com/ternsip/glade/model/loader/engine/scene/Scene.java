package com.ternsip.glade.model.loader.engine.scene;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import org.joml.Vector3f;


/**
 * Represents all the stuff in the scene (just the camera, light, and model
 * really).
 *
 * @author Karl
 */
public class Scene {

    private final Camera camera;

    private final AnimatedModel animatedModel;

    private Vector3f lightDirection = new Vector3f(0, -1, 0);

    public Scene(AnimatedModel model, Camera cam) {
        this.animatedModel = model;
        this.camera = cam;
    }

    /**
     * @return The scene's camera.
     */
    public Camera getCamera() {
        return camera;
    }

    public AnimatedModel getAnimatedModel() {
        return animatedModel;
    }

    /**
     * @return The direction of the light as a vector.
     */
    public Vector3f getLightDirection() {
        return lightDirection;
    }

    public void setLightDirection(Vector3f lightDir) {
        this.lightDirection.set(lightDir);
    }

}
