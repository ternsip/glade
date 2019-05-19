package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.universe.entities.base.Entity;
import org.joml.Vector3f;

import java.io.File;

public class EntityDude extends Entity {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/dude/dude.3ds"))
                .manualMeshMaterials((new Material[]{new Material(new Texture(new File("models/dude/dude.png")))}))
                .baseRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)))
                .build()
        );
    }

}
