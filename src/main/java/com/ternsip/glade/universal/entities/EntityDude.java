package com.ternsip.glade.universal.entities;

import com.ternsip.glade.universal.*;

import java.io.File;

public class EntityDude extends Entity {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/dude/dude.3ds")).manualMeshMaterials((new Material[]{new Material(new Texture(new File("models/dude/dude.png")))})).build());
    }

}
