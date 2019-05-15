package com.ternsip.glade.universal;

import com.sun.istack.internal.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector4f;

import java.io.File;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@Getter
public class Material {

    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private final Vector4f diffuseColour;
    private final Vector4f specularColour;
    private final @Nullable TextureAtlas.Texture diffuseMap;
    private final @Nullable TextureAtlas.Texture localNormalMap;
    private final @Nullable TextureAtlas.Texture specularMap;
    private final @Nullable TextureAtlas.Texture glowMap;

    public Material() {
        this(
                DEFAULT_COLOUR,
                DEFAULT_COLOUR,
                new File(""),
                new File(""),
                new File(""),
                new File("")
        );
    }

    public Material(File diffuseTexture) {
        this(
                DEFAULT_COLOUR,
                DEFAULT_COLOUR,
                diffuseTexture,
                new File(""),
                new File(""),
                new File("")
        );
    }

    public Material(
            Vector4f diffuseColour,
            Vector4f specularColour,
            File diffuseMap,
            File localNormalMap,
            File specularMap,
            File glowMap
    ) {
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.diffuseMap = diffuseMap.getPath().isEmpty() ? null : DISPLAY_MANAGER.getTextureAtlas().getTexture(diffuseMap);
        this.localNormalMap = localNormalMap.getPath().isEmpty()? null : DISPLAY_MANAGER.getTextureAtlas().getTexture(localNormalMap);
        this.specularMap = specularMap.getPath().isEmpty() ? null : DISPLAY_MANAGER.getTextureAtlas().getTexture(specularMap);
        this.glowMap = glowMap.getPath().isEmpty() ? null : DISPLAY_MANAGER.getTextureAtlas().getTexture(glowMap);
    }
}