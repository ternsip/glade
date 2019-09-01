package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.interfaces.IUniverse;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3fc;

/**
 * Class should be thread safe
 */
@Getter
@Setter
public abstract class Entity<T extends Effigy> extends Volumetric implements IUniverse {

    public void register() {
        getUniverse().getEntityRepository().register(this);
    }

    public void unregister() {
        getUniverse().getEntityRepository().unregister(this);
    }

    public void update(T effigy) {
        effigy.setPosition(getPosition());
        effigy.setRotation(getRotation());
        effigy.setScale(getScale());
        effigy.setVisible(isVisible());
    }

    // This method can be called only in graphics, it should be supplied
    public abstract T getEffigy();

    public void update() {
    }

    public Vector3fc getCameraAttachmentPoint() {
        return getPosition();
    }

}
