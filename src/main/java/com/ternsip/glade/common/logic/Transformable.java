package com.ternsip.glade.common.logic;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface Transformable {

    Vector3f getPosition();

    void setPosition(Vector3fc position);

    Vector3f getScale();

    void setScale(Vector3fc scale);

    Vector3f getRotation();

    void setRotation(Vector3fc rotation);

    void increasePosition(Vector3fc delta);

    void increaseRotation(Vector3fc delta);


}

