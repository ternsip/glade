package com.ternsip.glade.universal;

import lombok.*;
import lombok.experimental.Wither;

import java.io.File;

@Wither
@RequiredArgsConstructor
@Getter
public class Material {

    private final Texture texture;
    private final Texture diffuseMap;
    private final Texture specularMap;
    private final Texture ambientMap;
    private final Texture emissiveMap;
    private final Texture heightMap;
    private final Texture normalsMap;
    private final Texture shininessMap;
    private final Texture opacityMap;
    private final Texture displacementMap;
    private final Texture lightMap;
    private final Texture reflectionMap;

    public Material() {
        this(
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture()
        );
    }

    public Material(File texture) {
        this(
                new Texture(texture),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture(),
                new Texture()
        );
    }

}