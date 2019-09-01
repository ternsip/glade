package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.collisions.base.Collisions;

public interface ICollisions {

    LazyWrapper<Collisions> COLLISIONS = new LazyWrapper<>(Collisions::new);

    default Collisions getCollisions() {
        return COLLISIONS.getObjective();
    }

}
