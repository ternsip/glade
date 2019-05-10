package com.ternsip.glade.model.loader.engine.scene;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;


public class Scene {

    private final Camera camera;

    private final AnimatedModel animatedModel;

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


}
