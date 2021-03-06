package com.ternsip.glade.graphics.visual.impl.test;

import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;

import java.io.File;

public class EffigyLamp extends EffigyAnimated {

    public Model loadModel() {
        Material[] lampMaterials = new Material[]{
                new Material()
                        .withDiffuseMap(new Texture(new File("models/lamp/Diffuse.png")))
                        .withAmbientMap(new Texture(new File("models/lamp/ambient occlusion.png")))
                        .withEmissiveMap(new Texture(new File("models/lamp/emissive.jpg")))
                        .withSpecularMap(new Texture(new File("models/lamp/Specular.png")))
                        .withNormalsMap(new Texture(new File("models/lamp/normal.png")))
        };
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/lamp/crystal_lamp_ring.fbx")).manualMeshMaterials(lampMaterials).build());
    }

}
