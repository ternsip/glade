package com.ternsip.glade.universal.entities;

import com.ternsip.glade.universal.Entity;
import com.ternsip.glade.universal.Model;
import com.ternsip.glade.universal.ModelLoader;
import com.ternsip.glade.universal.Settings;

import java.io.File;

public class EntityZebra extends Entity {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/zebra/ZebraLOD1.ms3d")).build());
    }

}
