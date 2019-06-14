package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.common.logic.Transformable;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Class should be thread safe
 */
@Getter
@Setter
public abstract class Entity<T extends Effigy> implements Universal, Transformable {

    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);

    private boolean visualReloadRequired = false;

    public Entity() {
        getUniverse().getEntityRepository().register(this);
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

    public void increasePosition(Vector3fc delta) {
        position.add(delta);
    }

    public void increaseRotation(Vector3fc delta) {
        rotation.add(delta);
    }

    public void finish() {
        getUniverse().getEntityRepository().unregister(this);
    }

    public void update(T effigy) {
        effigy.setPosition(getPosition());
        effigy.setRotation(getRotation());
        effigy.setScale(getScale());
    }

    // This method can be called only in graphics, it should be supplied
    public abstract T getEffigy();

    public void update() {
    }

}
