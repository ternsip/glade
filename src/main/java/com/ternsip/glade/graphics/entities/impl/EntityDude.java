package com.ternsip.glade.graphics.entities.impl;

import com.ternsip.glade.graphics.entities.base.BaseFigure;
import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.graphics.entities.base.Entity;
import org.joml.Vector3f;

import java.io.File;

public class EntityDude extends BaseFigure {

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
