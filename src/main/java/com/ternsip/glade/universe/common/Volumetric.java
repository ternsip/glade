package com.ternsip.glade.universe.common;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Volumetric {

    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);

    public void setFromValues(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz) {
        this.position.set(px, py, pz);
        this.rotation.set(rx, ry, rz);
        this.scale.set(sx, sy, sz);
    }

    public void setFromVolumetric(Volumetric volumetric) {
        this.position.set(volumetric.getPosition());
        this.rotation.set(volumetric.getRotation());
        this.scale.set(volumetric.getScale());
    }

    public void setFromVolumetricInterpolated(VolumetricInterpolated volumetricInterpolated) {
        this.position.set(volumetricInterpolated.getPositionInterpolated());
        this.rotation.set(volumetricInterpolated.getRotationInterpolated());
        this.scale.set(volumetricInterpolated.getScaleInterpolated());
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

}
