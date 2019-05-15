package com.ternsip.glade.shader.uniforms;

import com.ternsip.glade.shader.base.Uniform;
import com.ternsip.glade.universal.Texture;

public class UniformTextureAddress extends Uniform<Texture> {

    private UniformBoolean isTexturePresent = new UniformBoolean();
    private UniformBoolean isColorPresent = new UniformBoolean();
    private UniformSampler2DArray atlasNumber = new UniformSampler2DArray();
    private UniformInteger layer = new UniformInteger();
    private UniformVec2 maxUV = new UniformVec2();
    private UniformVec4 color = new UniformVec4();

    @Override
    public void locate(int programID, String name) {
        isTexturePresent.locate(programID, name + "IsTexturePresent");
        isColorPresent.locate(programID, name + "IsColorPresent");
        atlasNumber.locate(programID, name + "AtlasNumber");
        layer.locate(programID, name + "Layer");
        maxUV.locate(programID, name + "MaxUV");
        color.locate(programID, name + "Color");
    }

    @Override
    public void load(Texture value) {
        isTexturePresent.load(value.isTexturePresent());
        isColorPresent.load(value.isColorPresent());
        color.load(value.getColor());
        atlasNumber.load(value.getAtlasTexture().getAtlasNumber());
        layer.load(value.getAtlasTexture().getLayer());
        maxUV.load(value.getAtlasTexture().getMaxUV());
    }
}
