package com.ternsip.glade.universe.entities.base;

import lombok.EqualsAndHashCode;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

@EqualsAndHashCode
public class Volumetric implements Serializable {

    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);
    private final AtomicBoolean visible = new AtomicBoolean(true);

    public void setFromAnother(Volumetric volumetric) {
        setPosition(volumetric.getPosition());
        setRotation(volumetric.getRotation());
        setScale(volumetric.getScale());
        setVisible(volumetric.isVisible());
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
        // TODO Add direct packet sending and cache them, collect into one entity and then pull
        this.rotation.set(rotation);
    }

    public boolean isVisible() {
        return visible.get();
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

}
