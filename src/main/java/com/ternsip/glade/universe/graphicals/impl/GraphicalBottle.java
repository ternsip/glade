package com.ternsip.glade.universe.graphicals.impl;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.universe.graphicals.base.GraphicalAnimated;

import java.io.File;

public class GraphicalBottle extends GraphicalAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/bottle/bottle.3ds")).build());
    }

}
