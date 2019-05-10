package com.ternsip.glade.model.loader.engine.textures;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.File;

public class Texture {

    public final int textureId;
    public final int size;
    private final int type;

    protected Texture(int textureId, int size) {
        this.textureId = textureId;
        this.size = size;
        this.type = GL11.GL_TEXTURE_2D;
    }

    protected Texture(int textureId, int type, int size) {
        this.textureId = textureId;
        this.size = size;
        this.type = type;
    }

    public static TextureBuilder newTexture(File textureFile) {
        return new TextureBuilder(textureFile);
    }

    public void bindToUnit(int unit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        GL11.glBindTexture(type, textureId);
    }

    public void delete() {
        GL11.glDeleteTextures(textureId);
    }

}
