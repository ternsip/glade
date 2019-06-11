package com.ternsip.glade.graphics.visual.base.graphical;

import org.joml.Vector3f;

public interface Transformable {

    Vector3f getPosition();

    void setPosition(Vector3f position);

    Vector3f getScale();

    void setScale(Vector3f scale);

    Vector3f getRotation();

    void setRotation(Vector3f rotation);

    void increasePosition(Vector3f delta);

    void increaseRotation(Vector3f delta);


}

