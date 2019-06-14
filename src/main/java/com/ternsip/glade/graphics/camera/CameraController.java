package com.ternsip.glade.graphics.camera;

import com.ternsip.glade.common.logic.Transformable;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

public interface CameraController {

    void update(Transformable target);

    Vector3fc getEyePosition();

    Matrix4fc getViewMatrix();

    void finish();

}
