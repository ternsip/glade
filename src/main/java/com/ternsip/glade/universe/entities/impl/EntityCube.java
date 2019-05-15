package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.entity.Cube;
import com.ternsip.glade.universal.Model;
import com.ternsip.glade.universe.entities.base.Entity;

public class EntityCube extends Entity {

    protected Model loadModel() {
        return new Model(Cube.generateMesh());
    }

}
