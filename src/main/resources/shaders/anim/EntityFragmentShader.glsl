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

uniform TextureData diffuseMap;
uniform TextureData specularMap;
uniform TextureData ambientMap;
uniform TextureData emissiveMap;
uniform TextureData heightMap;
uniform TextureData normalsMap;
uniform TextureData shininessMap;
uniform TextureData opacityMap;
uniform TextureData displacementMap;
uniform TextureData lightMap;
uniform TextureData reflectionMap;

// In fact it's light position normalized
uniform vec3 lightDirection;

vec4 getTextureColor(TextureData textureData, bool mainTexture) {
    bool force = !textureData.isColorPresent && !textureData.isTexturePresent && mainTexture;
    if (force || textureData.isTexturePresent) {
        vec4 texel = texture(textureData.atlasNumber, vec3(pass_textureCoords * textureData.maxUV, textureData.layer));
        return (force || textureData.isColorPresent) ? (texel * textureData.color) : texel;
    }
    return textureData.isColorPresent ? textureData.color : vec4(0, 0, 0, 0);
}

//https://www.neurobs.com/pres_docs/html/03_presentation/04_stimuli/03_visual_stimuli/02_picture_stimuli/04_3d_graphics/02_3d_common_properties/index.html
//https://github.com/lwjglgamedev/lwjglbook/blob/master/chapter28/src/main/resources/shaders/point_light_fragment.fs
void main(void){

    vec3 light_color = vec3(1, 1, 1);
    vec3 base_ambient = vec3(0.5, 0.5, 0.5);
    float ambient_multiplier = 0.5;
    float light_intensity = 1.0;
    float diffuseFactor = 0.6;

    vec3 unitNormal = normalize(pass_normal);
    float surfaceLight = max(dot(lightDirection, unitNormal), 0.0);

    // Diffuse color
    vec4 texColor = getTextureColor(diffuseMap, true);
    vec3 diffuseColor = texColor.xyz * light_color * light_intensity * surfaceLight;

    if (texColor.a < 0.1){
        discard;
    }

    // Ambient color
    vec4 ambientTexColor = getTextureColor(ambientMap, false);
    vec3 ambientColor = ambientTexColor.xyz * ambient_multiplier + base_ambient;

    // Emissive light
    vec4 emmissiveTexColor = getTextureColor(emissiveMap, false);
    vec3 emmissiveColor = emmissiveTexColor.xyz;

    // Specular Light
    vec4 specularTexColor = getTextureColor(specularMap, false);
    vec3 specColour = vec3(0, 0, 0);

    out_colour = vec4((diffuseColor + ambientColor + emmissiveColor + specColour) * texColor.xyz, texColor.a);

}