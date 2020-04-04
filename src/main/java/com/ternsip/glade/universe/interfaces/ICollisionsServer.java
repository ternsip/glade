package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.collisions.base.Collisions;

public interface ICollisionsServer {

    LazyWrapper<Collisions> COLLISIONS_SERVER = new LazyWrapper<>(Collisions::new);

    default Collisions getCollisionsServer() {
        return COLLISIONS_SERVER.getObjective();
    }

}
