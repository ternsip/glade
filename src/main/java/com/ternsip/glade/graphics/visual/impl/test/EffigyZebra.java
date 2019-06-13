package com.ternsip.glade.graphics.visual.impl.test;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;

import java.io.File;

public class EffigyZebra extends EffigyAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/zebra/ZebraLOD1.ms3d")).build());
    }

}
