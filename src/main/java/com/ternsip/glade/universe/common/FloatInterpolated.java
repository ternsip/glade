package com.ternsip.glade.universe.common;

import java.util.concurrent.atomic.AtomicReference;

public class FloatInterpolated extends Interpolated<AtomicReference<Float>> {

    public FloatInterpolated(Float value) {
        super(new AtomicReference<>(value), new AtomicReference<>(value));
    }

    public void update(float value) {
        getPrevValue().set(getLastValue().get());
        getPrevTime().set(getLastTime().get());
        getLastTime().set(getCurrentTime());
        getLastValue().set(value);
    }

    public float getValueInterpolated() {
        return interpolate(getLastValue().get(), getPrevValue().get());
    }

    public float getValue() {
        return getLastValue().get();
    }

}
