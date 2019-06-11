package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.graphical.Transformable;
import com.ternsip.glade.graphics.visual.base.graphical.Visual;
import lombok.Getter;
import org.joml.Vector3f;

/**
 * Class should be thread safe
 */
@Getter
public abstract class EntityTransformable<T extends Visual & Transformable> extends Entity<T> implements Transformable {

    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);

    public void update(T visual) {
        visual.setPosition(getPosition());
        visual.setRotation(getRotation());
        visual.setScale(getScale());
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public void increasePosition(Vector3f delta) {
        position.add(delta);
    }

    public void increaseRotation(Vector3f delta) {
        rotation.add(delta);
    }

}
