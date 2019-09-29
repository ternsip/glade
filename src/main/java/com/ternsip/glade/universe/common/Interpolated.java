package com.ternsip.glade.universe.common;

import com.ternsip.glade.common.logic.Maths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Getter
public abstract class Interpolated<T> {

    private final T lastValue;
    private final T prevValue;
    private final AtomicLong lastTime = new AtomicLong(getCurrentTime());
    private final AtomicLong prevTime = new AtomicLong(getCurrentTime());

    public float interpolate(float last, float prev) {
        float blend = getBlend();
        return last + (prev - last) * blend;
    }

    public Vector3fc interpolate(Vector3fc last, Vector3fc prev) {
        float blend = getBlend();
        return new Vector3f(
                last.x() + (prev.x() - last.x()) * blend,
                last.y() + (prev.y() - last.y()) * blend,
                last.z() + (prev.z() - last.z()) * blend
        );
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private float getBlend() {
        long timeGap = getLastTime().get() - getPrevTime().get();
        long timeSpent = getCurrentTime() - getLastTime().get();
        return timeGap <= 0 ? 0 : Maths.clamp(0f, 1f, (timeGap - timeSpent) / ((float) timeGap));
    }

}
