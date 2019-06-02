package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ModelLoader;
import com.ternsip.glade.graphics.general.Settings;
import com.ternsip.glade.universe.entities.base.EntityDefault;
import org.joml.Vector3f;

import java.io.File;

public class EntityHagrid extends EntityDefault {

    protected Model loadModel() {
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/bob/boblamp.md5mesh"))
                .animationFile(new File("models/bob/boblamp.md5anim"))
                .baseRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)))
                .build()
        );

    }

    @Override
    public void update() {
        this.increaseRotation(new Vector3f(0, 0.01f, 0.00f));
    }

}
