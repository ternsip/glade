package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.camera.Camera;

public interface ICamera {

    LazyWrapper<Camera> CAMERA = new LazyWrapper<>(Camera::new);

    default Camera getCamera() {
        return CAMERA.getObjective();
    }

}
