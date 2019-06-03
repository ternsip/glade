package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.universe.graphicals.base.Visual;

import static com.ternsip.glade.Glade.UNIVERSE;

/**
 * Class should be thread safe
 */
public abstract class Entity {

    public Entity() {
        UNIVERSE.getEntityRepository().getEntities().add(this);
    }

    public void finish() {
        UNIVERSE.getEntityRepository().getEntities().remove(this);
    }

    public abstract Visual getVisual();

}
