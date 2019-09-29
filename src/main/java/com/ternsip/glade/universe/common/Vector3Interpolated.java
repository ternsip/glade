package com.ternsip.glade.universe.common;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Vector3Interpolated extends Interpolated<Vector3f> {

    public Vector3Interpolated(Vector3f value) {
        super(new Vector3f(value), new Vector3f(value));
    }

    public void update(Vector3fc value) {
        getPrevValue().set(getLastValue());
        getPrevTime().set(getLastTime().get());
        getLastTime().set(getCurrentTime());
        getLastValue().set(value);
    }

    public Vector3fc getValueInterpolated() {
        return interpolate(getLastValue(), getPrevValue());
    }

    public Vector3fc getValue() {
        return getLastValue();
    }

}
