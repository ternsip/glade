package com.ternsip.glade.universe.graphicals.impl;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.universe.graphicals.base.GraphicalAnimated;
import org.joml.Vector3f;

import java.io.File;

public class GraphicalWarrior extends GraphicalAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/warrior/warrior.3ds"))
                .baseRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)))
                .build()
        );
    }

}
