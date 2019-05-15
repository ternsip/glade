package com.ternsip.glade.universal.entities;

import com.ternsip.glade.universal.Entity;
import com.ternsip.glade.universal.Model;
import com.ternsip.glade.universal.ModelLoader;
import com.ternsip.glade.universal.Settings;

import java.io.File;

public class EntityBoy extends Entity {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/boy/boy.dae")).build());
    }

}
