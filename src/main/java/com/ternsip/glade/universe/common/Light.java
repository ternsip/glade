package com.ternsip.glade.universe.common;

import org.joml.Vector3f;

public interface Light {

    Vector3f getPosition();

    float getIntensity();

    Vector3f getColor();

}
