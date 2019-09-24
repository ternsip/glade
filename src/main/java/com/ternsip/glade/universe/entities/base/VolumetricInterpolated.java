package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.common.logic.Maths;
import lombok.Getter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class VolumetricInterpolated {

    private final Volumetric lastVolumetric = new Volumetric();
    private final Volumetric prevVolumetric = new Volumetric();
    private final AtomicLong lastTime = new AtomicLong(getCurrentTime());
    private final AtomicLong prevTime = new AtomicLong(getCurrentTime());

    public void update(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz, boolean visible) {
        getPrevVolumetric().setFromVolumetric(getLastVolumetric());
        getPrevTime().set(getLastTime().get());
        getLastTime().set(getCurrentTime());
        getLastVolumetric().setFromValues(px, py, pz, rx, ry, rz, sx, sy, sz, visible);
    }

    public Vector3fc getPositionInterpolated() {
        return interpolate(getLastVolumetric().getPosition(), getPrevVolumetric().getPosition());
    }

    public Vector3fc getScaleInterpolated() {
        return interpolate(getLastVolumetric().getScale(), getPrevVolumetric().getScale());
    }

    public Vector3fc getRotationInterpolated() {
        return interpolate(getLastVolumetric().getRotation(), getPrevVolumetric().getRotation());
    }

    public boolean isVisibleInterpolated() {
        // TODO make fade away effect (alpha 0f-1f) interpolated
        return getLastVolumetric().isVisible();
    }

    public void setPosition(Vector3fc position) {
        getLastVolumetric().setPosition(position);
    }

    public void setScale(Vector3fc scale) {
        getLastVolumetric().setScale(scale);
    }

    public void setRotation(Vector3fc rotation) {
        getLastVolumetric().setRotation(rotation);
    }

    public void setVisible(boolean visible) {
        getLastVolumetric().setVisible(visible);
    }

    public Vector3fc getPosition() {
        return getLastVolumetric().getPosition();
    }

    public Vector3fc getScale() {
        return getLastVolumetric().getScale();
    }

    public Vector3fc getRotation() {
        return getLastVolumetric().getRotation();
    }

    public boolean isVisible() {
        return getLastVolumetric().isVisible();
    }

    private Vector3fc interpolate(Vector3fc last, Vector3fc prev) {
        long timeGap = getLastTime().get() - getPrevTime().get();
        long timeSpent = getCurrentTime() - getLastTime().get();
        float blend = timeGap <= 0 ? 0 : Maths.bound(0f, 1f, (timeGap - timeSpent) / ((float) timeGap));
        return new Vector3f(
                last.x() + (prev.x() - last.x()) * blend,
                last.y() + (prev.y() - last.y()) * blend,
                last.z() + (prev.z() - last.z()) * blend
        );
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

}
