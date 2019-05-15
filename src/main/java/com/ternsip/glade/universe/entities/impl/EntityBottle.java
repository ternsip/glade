package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universal.Model;
import com.ternsip.glade.universal.ModelLoader;
import com.ternsip.glade.universal.Settings;
import com.ternsip.glade.universe.entities.base.Entity;

import java.io.File;

public class EntityBottle extends Entity {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/bottle/bottle.3ds")).build());
    }

}
