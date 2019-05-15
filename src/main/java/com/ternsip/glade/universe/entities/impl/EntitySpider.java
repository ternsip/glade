package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universal.*;
import com.ternsip.glade.universe.entities.base.Entity;

import java.io.File;

public class EntitySpider extends Entity {

    protected Model loadModel() {
        Material[] spiderMaterials = new Material[]{new Material().withDiffuseMap(new Texture(new File("models/spider/Spinnen_Bein_tex_2.jpg")))};
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/spider/spider.dae")).manualMeshMaterials(spiderMaterials).build());
    }

}
