package com.ternsip.glade.model.loader.engine.textures;


import lombok.Getter;

import java.io.File;

@Getter
public class TextureBuilder {

    private boolean clampEdges = false;
    private boolean mipmap = false;
    private boolean anisotropic = true;
    private boolean nearest = false;

    private File file;

    protected TextureBuilder(File textureFile) {
        this.file = textureFile;
    }

    public Texture create() {
        TextureData textureData = TextureUtils.decodeTextureFile(file);
        int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
        return new Texture(textureId, textureData.getWidth());
    }

    public TextureBuilder clampEdges() {
        this.clampEdges = true;
        return this;
    }

    public TextureBuilder normalMipMap() {
        this.mipmap = true;
        this.anisotropic = false;
        return this;
    }

    public TextureBuilder nearestFiltering() {
        this.mipmap = false;
        this.anisotropic = false;
        this.nearest = true;
        return this;
    }

    public TextureBuilder anisotropic() {
        this.mipmap = true;
        this.anisotropic = true;
        return this;
    }

}
