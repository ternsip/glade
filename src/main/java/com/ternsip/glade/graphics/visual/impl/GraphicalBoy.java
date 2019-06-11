package com.ternsip.glade.graphics.visual.impl;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.graphics.visual.base.GraphicalAnimated;
import org.joml.Vector3f;

import java.io.File;

public class GraphicalBoy extends GraphicalAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/boy/boy.dae"))
                .baseRotation(new Vector3f(0, 0, (float) (-Math.PI / 2.0f)))
                .build()
        );
    }

}
