#version 400 core

struct TextureData {
    bool isTexturePresent;
    bool isColorPresent;
    vec4 color;
    sampler2DArray atlasNumber;
    int layer;
    vec2 maxUV;
};

in vec2 pass_textureCoords;
in vec3 pass_normal;

out vec4 out_colour;

uniform TextureData fontTexture;

void main(void) {

    out_colour = texture(fontTexture.atlasNumber, vec3(pass_textureCoords * textureMap.maxUV, textureMap.layer));

}