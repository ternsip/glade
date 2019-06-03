package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.graphicals.base.Visual;
import com.ternsip.glade.universe.graphicals.impl.GraphicalFps;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityFps extends Entity {

    @Override
    public Visual getVisual() {
        return new GraphicalFps(100);
    }

}
