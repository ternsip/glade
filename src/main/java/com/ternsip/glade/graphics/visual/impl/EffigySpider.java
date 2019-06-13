package com.ternsip.glade.graphics.visual.impl;

import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import org.joml.Vector3f;

import java.io.File;

public class EffigySpider extends EffigyAnimated {

    public Model loadModel() {
        Material[] spiderMaterials = new Material[]{new Material().withDiffuseMap(new Texture(new File("models/spider/Spinnen_Bein_tex_2.jpg")))};
        return ModelLoader.loadModel(Settings
                .builder()
                .meshFile(new File("models/spider/spider.dae"))
                .manualMeshMaterials(spiderMaterials)
                .baseRotation(new Vector3f(0, 0, (float) (-Math.PI / 2)))
                .build()
        );
    }

}
