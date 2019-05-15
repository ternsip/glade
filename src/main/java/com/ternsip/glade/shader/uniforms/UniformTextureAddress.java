package com.ternsip.glade.shader.uniforms;

import com.ternsip.glade.shader.base.Uniform;
import com.ternsip.glade.universal.TextureAtlas;

public class UniformTextureAddress extends Uniform<TextureAtlas.Texture> {

    private UniformBoolean isPresent = new UniformBoolean();
    private UniformSampler2DArray atlasNumber = new UniformSampler2DArray();
    private UniformInteger layer = new UniformInteger();
    private UniformVec2 maxUV = new UniformVec2();

    @Override
    public void locate(int programID, String name) {
        isPresent.locate(programID, name + "IsPresent");
        atlasNumber.locate(programID, name + "AtlasNumber");
        layer.locate(programID, name + "Layer");
        maxUV.locate(programID, name + "MaxUV");
    }

    @Override
    public void load(TextureAtlas.Texture value) {
        if (value == null) {
            isPresent.load(false);
            return;
        }
        isPresent.load(true);
        atlasNumber.load(value.getAtlasNumber());
        layer.load(value.getLayer());
        maxUV.load(value.getMaxUV());
    }
}
