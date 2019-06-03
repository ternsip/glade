package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.universe.entities.base.GraphicalDefault;

import java.io.File;

public class GraphicalBoy extends GraphicalDefault {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/boy/boy.dae"))
                .build()
        );
    }

}
