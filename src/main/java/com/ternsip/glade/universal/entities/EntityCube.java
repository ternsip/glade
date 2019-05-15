package com.ternsip.glade.universal.entities;

import com.ternsip.glade.entity.Cube;
import com.ternsip.glade.universal.Entity;
import com.ternsip.glade.universal.Model;

public class EntityCube extends Entity {

    protected Model loadModel() {
        return new Model(Cube.generateMesh());
    }

}
