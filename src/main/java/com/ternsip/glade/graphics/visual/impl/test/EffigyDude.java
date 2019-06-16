package com.ternsip.glade.graphics.visual.impl.test;

import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import org.joml.Vector3f;

import java.io.File;

public class EffigyDude extends EffigyAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/dude/dude.3ds"))
                .manualMeshMaterials((new Material[]{new Material(new Texture(new File("models/dude/dude.png")))}))
                .baseRotation(new Vector3f((float) (-Math.PI / 2), 0, 0))
                .build()
        );
    }

}
