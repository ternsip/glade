package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

@Wither
@RequiredArgsConstructor
@Getter
public class Material {

    // Diffuse texture a.k.a Main texture of the object
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
                new Texture()
        );
    }

    public Material(Texture textureMap) {
        this(
                textureMap,
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