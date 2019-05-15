package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.universe.entities.base.Entity;

import java.io.File;

public class EntityBottle extends Entity {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/bottle/bottle.3ds")).build());
    }

}
