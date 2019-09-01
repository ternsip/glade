package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.camera.CameraController;

public interface ICameraController {

    LazyWrapper<CameraController> CAMERA_CONTROLLER = new LazyWrapper<>(CameraController::new);

    default CameraController getCameraController() {
        return CAMERA_CONTROLLER.getObjective();
    }

}
