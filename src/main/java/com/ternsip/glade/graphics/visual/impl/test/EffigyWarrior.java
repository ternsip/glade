package com.ternsip.glade.graphics.visual.impl.test;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import org.joml.Vector3f;

import java.io.File;

public class EffigyWarrior extends EffigyAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/warrior/warrior.3ds"))
                .baseRotation(new Vector3f((float) (-Math.PI / 2), 0, 0))
                .build()
        );
    }

}
