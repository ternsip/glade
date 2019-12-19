#version 130

struct TextureData {
    bool isTexturePresent;
    bool isColorPresent;
    vec4 color;
    sampler2DArray atlasNumber;
    int layer;
    vec2 maxUV;
};

in vec2 pass_textureCoords;

out vec4 out_colour;

uniform TextureData diffuseMap;

vec4 getTextureColor(TextureData textureData, bool mainTexture) {
    bool force = !textureData.isColorPresent && !textureData.isTexturePresent && mainTexture;
    if (force || textureData.isTexturePresent) {
        vec4 texel = texture(textureData.atlasNumber, vec3(pass_textureCoords * textureData.maxUV, textureData.layer));
        return (force || textureData.isColorPresent) ? (texel * textureData.color) : texel;
    }
    return textureData.isColorPresent ? textureData.color : vec4(0, 0, 0, 0);
}

void main(void){

    out_colour = getTextureColor(diffuseMap, true);

}