package com.ternsip.glade.graphics.entities.base;

import com.ternsip.glade.graphics.renderer.base.Renderer;
import org.joml.Vector3f;

public interface Figure {

    void setPosition(Vector3f position);
    void setScale(Vector3f scale);
    void setRotation(Vector3f rotation);
    void increasePosition(Vector3f delta);
    void increaseRotation(Vector3f delta);
    Class<? extends Renderer> getRenderer();
    void update();
    void finish();

}
