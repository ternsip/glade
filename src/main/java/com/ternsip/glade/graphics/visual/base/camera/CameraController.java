package com.ternsip.glade.graphics.visual.base.camera;

import com.ternsip.glade.graphics.visual.base.graphical.Transformable;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

public interface CameraController {

    void update(Transformable target);

    Vector3fc getEyePosition();

    Matrix4fc getViewMatrix();

    void finish();

}
