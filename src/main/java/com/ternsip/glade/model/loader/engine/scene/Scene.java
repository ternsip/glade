package com.ternsip.glade.model.loader.engine.scene;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import org.joml.Vector3f;


public class Scene {

    private final Camera camera;

    private final AnimatedModel animatedModel;

    private Vector3f lightDirection = new Vector3f(0, -1, 0);

    public Scene(AnimatedModel model, Camera cam) {
        this.animatedModel = model;
        this.camera = cam;
    }

    public Camera getCamera() {
        return camera;
    }

    public AnimatedModel getAnimatedModel() {
        return animatedModel;
    }

    public Vector3f getLightDirection() {
        return lightDirection;
    }

    public void setLightDirection(Vector3f lightDir) {
        this.lightDirection.set(lightDir);
    }

}
