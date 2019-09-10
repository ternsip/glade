package com.ternsip.glade.universe.entities.base;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Volumetric implements Serializable {

    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);
    private final AtomicBoolean visible = new AtomicBoolean(true);
    private final AtomicLong lastTimeChanged = new AtomicLong(getCurrentTime());

    public void setFromVolumetric(Volumetric volumetric) {
        this.position.set(volumetric.getPosition());
        this.rotation.set(volumetric.getRotation());
        this.scale.set(volumetric.getScale());
        this.visible.set(volumetric.isVisible());
        this.lastTimeChanged.set(volumetric.getLastTimeChanged());
    }

    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }

    public void setScale(Vector3fc scale) {
        this.scale.set(scale);
    }

    public void setRotation(Vector3fc rotation) {
        this.rotation.set(rotation);
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    public void updateTime() {
        this.lastTimeChanged.set(getCurrentTime());
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getScale() {
        return scale;
    }

    public Vector3fc getRotation() {
        return rotation;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public long getLastTimeChanged() {
        return lastTimeChanged.get();
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }


}
