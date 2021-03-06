package com.ternsip.glade.graphics.visual.impl.test;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import org.joml.Vector3f;

import java.io.File;

public class EffigyHagrid extends EffigyAnimated {

    public Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/bob/boblamp.md5mesh"))
                .animationFiles(new File[]{new File("models/bob/boblamp.md5anim")})
                .baseRotation(new Vector3f(0, 0, 0))
                .build()
        );
    }

}
