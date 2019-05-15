package com.ternsip.glade.universal;

import com.sun.istack.internal.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;
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
        this(null, null);
    }

    public Texture(File file) {
        this(null, file);
    }

    public Texture(Vector4f color) {
        this(color, null);
    }

    public Texture(@Nullable Vector4f color, @Nullable File file) {
        this.texturePresent = file != null;
        this.colorPresent = color != null;
        this.color = colorPresent ? color : DEFAULT_COLOR;
        this.atlasTexture = DISPLAY_MANAGER.getTextureAtlas().getTexture(this.texturePresent ? file : MISSING_TEXTURE);
    }

}
