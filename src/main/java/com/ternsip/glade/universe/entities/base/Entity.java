package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.graphicals.base.Visual;
import lombok.Getter;

/**
 * Class should be thread safe
 */
@Getter
public abstract class Entity<T extends Visual> implements Universal {

    public Entity() {
        getUniverse().getEntityRepository().register(this);
    }

    public void finish() {
        getUniverse().getEntityRepository().unregister(this);
    }

    public abstract T getVisual();

    public abstract void update(T visual);

    public abstract void update();

}
