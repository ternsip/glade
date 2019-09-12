package com.ternsip.glade.universe.entities.base;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Volumetric implements VolumetricSetters, Serializable {

    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);
    private final AtomicBoolean visible = new AtomicBoolean(true);
    private final AtomicLong lastTimeChanged = new AtomicLong(getCurrentTime());

    public void setFromVolumetric(Volumetric volumetric) {
        setPosition(volumetric.getPosition());
        setRotation(volumetric.getRotation());
        setScale(volumetric.getScale());
        setVisible(volumetric.isVisible());
        setLastTimeChanged(volumetric.getLastTimeChanged());
    }

    public void setFromVolumetricInterpolated(VolumetricInterpolated volumetricInterpolated) {
        setPosition(volumetricInterpolated.getPositionInterpolated());
        setRotation(volumetricInterpolated.getRotationInterpolated());
        setScale(volumetricInterpolated.getScaleInterpolated());
        setVisible(volumetricInterpolated.isVisibleInterpolated());
        setLastTimeChanged(volumetricInterpolated.getCurVolumetric().getLastTimeChanged());
    }

    public void updateTime() {
        setLastTimeChanged(getCurrentTime());
    }

    public Vector3fc getPosition() {
        return position;
    }

    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }

    public Vector3fc getScale() {
        return scale;
    }

    public void setScale(Vector3fc scale) {
        this.scale.set(scale);
    }

    public Vector3fc getRotation() {
        return rotation;
    }

    public void setRotation(Vector3fc rotation) {
        this.rotation.set(rotation);
    }

    public boolean isVisible() {
        return visible.get();
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    public long getLastTimeChanged() {
        return lastTimeChanged.get();
    }

    public void setLastTimeChanged(long time) {
        this.lastTimeChanged.set(time);
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }


}
