package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.collisions.base.Collisions;

public interface ICollisionsClient {

    LazyWrapper<Collisions> COLLISIONS_CLIENT = new LazyWrapper<>(Collisions::new);

    default Collisions getCollisionsClient() {
        return COLLISIONS_CLIENT.getObjective();
    }

}
