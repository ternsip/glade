package com.ternsip.glade.universal;

import lombok.Getter;
import org.joml.Vector4f;

import java.io.File;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.universal.TextureAtlas.MISSING_TEXTURE;

@Getter
public class Texture {

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private final boolean texturePresent;
    private final boolean colorPresent;
    private final Vector4f color;
    private final TextureAtlas.Texture atlasTexture;

    public Texture() {
        this(DEFAULT_COLOR, new File(""));
    }

    public Texture(File file) {
        this(DEFAULT_COLOR, file);
    }

    public Texture(Vector4f color) {
        this(DEFAULT_COLOR, new File(""));
    }

    public Texture(Vector4f color, File file) {
        this.texturePresent = !file.getPath().isEmpty();
        this.colorPresent = color.equals(DEFAULT_COLOR, 1f-4);
        this.color = color;
        this.atlasTexture = DISPLAY_MANAGER.getTextureAtlas().getTexture(this.texturePresent ? file : MISSING_TEXTURE);
    }

}
