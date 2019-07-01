package com.ternsip.glade.common.logic;

import org.joml.Vector3fc;

public interface Transformable {

    Vector3fc getPosition();

    void setPosition(Vector3fc position);

    Vector3fc getScale();

    void setScale(Vector3fc scale);

    Vector3fc getRotation();

    void setRotation(Vector3fc rotation);

    void increasePosition(Vector3fc delta);

    void increaseRotation(Vector3fc delta);


}

