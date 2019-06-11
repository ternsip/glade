package com.ternsip.glade.graphics.general;

import com.sun.istack.internal.Nullable;
import com.ternsip.glade.graphics.display.Displayable;
import com.ternsip.glade.graphics.visual.repository.TextureRepository;
import lombok.Getter;
import org.joml.Vector4f;

import java.io.File;

import static com.ternsip.glade.graphics.visual.repository.TextureRepository.MISSING_TEXTURE;

@Getter
public class Texture implements Displayable {

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private final boolean texturePresent;
    private final boolean colorPresent;
    private final Vector4f color;
    private final TextureRepository.Texture atlasTexture;

    public Texture() {
        this(null, null);
    }

    public Texture(TextureRepository.AtlasDecoder atlasDecoder) {
        this(atlasDecoder.getAtlasDirectory());
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
        this.atlasTexture = getDisplayManager().getGraphicalRepository().getTextureRepository().getTexture(this.texturePresent ? file : MISSING_TEXTURE);
    }

}
