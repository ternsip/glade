package com.ternsip.glade.graphics.entities.impl;

import com.ternsip.glade.graphics.entities.base.BaseFigure;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.graphics.entities.base.Entity;

import java.io.File;

public class EntityBottle extends BaseFigure {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/bottle/bottle.3ds")).build());
    }

}
