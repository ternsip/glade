package com.ternsip.glade.graphics.entities.impl;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.graphics.entities.base.Entity;
import org.joml.Vector3f;

import java.io.File;

public class FigureWarrior extends Entity {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/warrior/warrior.3ds"))
                .baseRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)))
                .build()
        );
    }

}