package com.ternsip.glade.universe.common;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Volumetric {

    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);
    private final AtomicBoolean visible = new AtomicBoolean(true);
    private final AtomicReference<Float> skyIntensity = new AtomicReference<Float>(1f);
    private final AtomicReference<Float> emitIntensity = new AtomicReference<Float>(1f);

    public void setFromValues(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz, boolean visible, float skyIntensity, float emitIntensity) {
        this.position.set(px, py, pz);
        this.rotation.set(rx, ry, rz);
        this.scale.set(sx, sy, sz);
        this.visible.set(visible);
        this.skyIntensity.set(skyIntensity);
        this.emitIntensity.set(emitIntensity);
    }

    public void setFromVolumetric(Volumetric volumetric) {
        this.position.set(volumetric.getPosition());
        this.rotation.set(volumetric.getRotation());
        this.scale.set(volumetric.getScale());
        this.visible.set(volumetric.isVisible());
        this.skyIntensity.set(volumetric.getSkyIntensity());
        this.emitIntensity.set(volumetric.getEmitIntensity());
    }

    public void setFromVolumetricInterpolated(VolumetricInterpolated volumetricInterpolated) {
        this.position.set(volumetricInterpolated.getPositionInterpolated());
        this.rotation.set(volumetricInterpolated.getRotationInterpolated());
        this.scale.set(volumetricInterpolated.getScaleInterpolated());
        this.visible.set(volumetricInterpolated.isVisibleInterpolated());
    }

    public float getSkyIntensity() {
        return this.skyIntensity.get();
    }

    public void setSkyIntensity(float skyIntensity) {
        this.skyIntensity.set(skyIntensity);
    }

    public float getEmitIntensity() {
        return this.emitIntensity.get();
    }

    public void setEmitIntensity(float emitIntensity) {
        this.emitIntensity.set(emitIntensity);
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

}
