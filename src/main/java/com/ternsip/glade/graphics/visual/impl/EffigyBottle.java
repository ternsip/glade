package com.ternsip.glade.graphics.visual.impl;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.graphics.visual.base.graphical.EffigyAnimated;

import java.io.File;

public class EffigyBottle extends EffigyAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/bottle/bottle.3ds")).build());
    }

}
