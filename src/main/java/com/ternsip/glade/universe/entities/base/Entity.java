package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.universe.graphicals.base.Visual;
import lombok.Getter;

import static com.ternsip.glade.Glade.UNIVERSE;

/**
 * Class should be thread safe
 */
@Getter
public abstract class Entity<T extends Visual> {

    public Entity() {
        UNIVERSE.getEntityRepository().register(this);
    }

    public void finish() {
        UNIVERSE.getEntityRepository().unregister(this);
    }

    public abstract T getVisual();

    public abstract void update(T visual);

    public abstract void update();

}
