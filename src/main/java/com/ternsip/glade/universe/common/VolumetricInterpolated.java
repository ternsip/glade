package com.ternsip.glade.universe.common;

import lombok.Getter;
import org.joml.Vector3fc;

@Getter
public class VolumetricInterpolated extends Interpolated<Volumetric> {

    public VolumetricInterpolated() {
        super(new Volumetric(), new Volumetric());
    }

    public void update(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz) {
        getPrevValue().setFromVolumetric(getLastValue());
        getPrevTime().set(getLastTime().get());
        getLastTime().set(getCurrentTime());
        getLastValue().setFromValues(px, py, pz, rx, ry, rz, sx, sy, sz);
    }

    public Vector3fc getPositionInterpolated() {
        return interpolate(getLastValue().getPosition(), getPrevValue().getPosition());
    }

    public Vector3fc getScaleInterpolated() {
        return interpolate(getLastValue().getScale(), getPrevValue().getScale());
    }

    public Vector3fc getRotationInterpolated() {
        return interpolate(getLastValue().getRotation(), getPrevValue().getRotation());
    }

    public Vector3fc getPosition() {
        return getLastValue().getPosition();
    }

    public void setPosition(Vector3fc position) {
        getLastValue().setPosition(position);
    }

    public Vector3fc getScale() {
        return getLastValue().getScale();
    }

    public void setScale(Vector3fc scale) {
        getLastValue().setScale(scale);
    }

    public Vector3fc getRotation() {
        return getLastValue().getRotation();
    }

    public void setRotation(Vector3fc rotation) {
        getLastValue().setRotation(rotation);
    }

}
