package com.ternsip.glade.universal.entities;

import com.ternsip.glade.universal.*;

import java.io.File;

public class EntityLamp extends Entity {

    protected Model loadModel() {
        Material[] lampMaterials = new Material[]{
                new Material()
                        .withTextureMap(new Texture(new File("models/lamp/color.png")))
                        .withDiffuseMap(new Texture(new File("models/lamp/Diffuse.png")))
                        .withAmbientMap(new Texture(new File("models/lamp/ambient occlusion.png")))
                        .withEmissiveMap(new Texture(new File("models/lamp/emissive.jpg")))
                        .withSpecularMap(new Texture(new File("models/lamp/Specular.png")))
                        .withNormalsMap(new Texture(new File("models/lamp/normal.png")))
        };
        return ModelLoader.loadModel(Settings.builder().meshFile(new File("models/lamp/crystal_lamp_ring.fbx")).manualMeshMaterials(lampMaterials).build());
    }

}
