package com.ternsip.glade.universe.common;

import org.joml.Vector3fc;

public interface Light {

    Vector3fc getPosition();

    float getIntensity();

    Vector3fc getColor();

}
