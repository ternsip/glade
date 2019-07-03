package com.ternsip.glade.graphics.camera;

import com.ternsip.glade.universe.entities.base.Entity;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

public interface CameraController {

    void update(Entity target);

    Vector3fc getEyePosition();

    Matrix4fc getViewMatrix();

    void finish();

}
