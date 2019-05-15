#version 400 core

in vec2 pass_textureCoords;
in vec3 pass_normal;

out vec4 out_colour;

uniform bool                textureIsTexturePresent;
uniform bool                textureIsColorPresent;
uniform vec4                textureColor;
uniform sampler2DArray      textureAtlasNumber;
uniform int                 textureLayer;
uniform vec2                textureMaxUV;

uniform bool                diffuseMapIsTexturePresent;
uniform bool                diffuseMapIsColorPresent;
uniform vec4                diffuseMapColor;
uniform sampler2DArray      diffuseMapAtlasNumber;
uniform int                 diffuseMapLayer;
uniform vec2                diffuseMapMaxUV;

uniform bool                specularMapIsTexturePresent;
uniform bool                specularMapIsColorPresent;
uniform vec4                specularMapColor;
uniform sampler2DArray      specularMapAtlasNumber;
uniform int                 specularMapLayer;
uniform vec2                specularMapMaxUV;

uniform bool                ambientMapIsTexturePresent;
uniform bool                ambientMapIsColorPresent;
uniform vec4                ambientMapColor;
uniform sampler2DArray      ambientMapAtlasNumber;
uniform int                 ambientMapLayer;
uniform vec2                ambientMapMaxUV;

uniform bool                emissiveMapIsTexturePresent;
uniform bool                emissiveMapIsColorPresent;
uniform vec4                emissiveMapColor;
uniform sampler2DArray      emissiveMapAtlasNumber;
uniform int                 emissiveMapLayer;
uniform vec2                emissiveMapMaxUV;

uniform bool                heightMapIsTexturePresent;
uniform bool                heightMapIsColorPresent;
uniform vec4                heightMapColor;
uniform sampler2DArray      heightMapAtlasNumber;
uniform int                 heightMapLayer;
uniform vec2                heightMapMaxUV;

uniform bool                normalsMapIsTexturePresent;
uniform bool                normalsMapIsColorPresent;
uniform vec4                normalsMapColor;
uniform sampler2DArray      normalsMapAtlasNumber;
uniform int                 normalsMapLayer;
uniform vec2                normalsMapMaxUV;

uniform bool                shininessMapIsTexturePresent;
uniform bool                shininessMapIsColorPresent;
uniform vec4                shininessMapColor;
uniform sampler2DArray      shininessMapAtlasNumber;
uniform int                 shininessMapLayer;
uniform vec2                shininessMapMaxUV;

uniform bool                opacityMapIsTexturePresent;
uniform bool                opacityMapIsColorPresent;
uniform vec4                opacityMapColor;
uniform sampler2DArray      opacityMapAtlasNumber;
uniform int                 opacityMapLayer;
uniform vec2                opacityMapMaxUV;

uniform bool                displacementMapIsTexturePresent;
uniform bool                displacementMapIsColorPresent;
uniform vec4                displacementMapColor;
uniform sampler2DArray      displacementMapAtlasNumber;
uniform int                 displacementMapLayer;
uniform vec2                displacementMapMaxUV;

uniform bool                lightMapIsTexturePresent;
uniform bool                lightMapIsColorPresent;
uniform vec4                lightMapColor;
uniform sampler2DArray      lightMapAtlasNumber;
uniform int                 lightMapLayer;
uniform vec2                lightMapMaxUV;

uniform bool                reflectionMapIsTexturePresent;
uniform bool                reflectionMapIsColorPresent;
uniform vec4                reflectionMapColor;
uniform sampler2DArray      reflectionMapAtlasNumber;
uniform int                 reflectionMapLayer;
uniform vec2                reflectionMapMaxUV;

uniform vec3 lightDirection;

vec4 getTextureColor(bool isColorPresent, bool isTexturePresent, sampler2DArray atlas, vec2 uv, vec2 maxUV, int layer, vec4 color) {
    if (isTexturePresent) {
        vec4 texel = texture(atlas, vec3(uv * maxUV, layer));
        return isColorPresent ? (texel * color) : texel;
    }
    return isColorPresent ? color : vec4(0, 0, 0, 0);
}

vec4 getMainTextureColor(bool isColorPresent, bool isTexturePresent, sampler2DArray atlas, vec2 uv, vec2 maxUV, int layer, vec4 color) {
    if (!isColorPresent && !isTexturePresent) {
        return getTextureColor(true, true, atlas, uv, maxUV, layer, color);
    }
    return getTextureColor(isColorPresent, isTexturePresent, atlas, uv, maxUV, layer, color);
}

// TODO turn into structures
void main(void){

    vec3 light_color = vec3(1, 1, 1);
    vec3 base_ambient = vec3(0.5, 0.5, 0.5);
    float ambient_multiplier = 0.5;
    float light_intensity = 1.0;
    float diffuseFactor = 0.6;

    vec3 unitNormal = normalize(pass_normal);
    float surfaceLight = max(dot(lightDirection, unitNormal), 0.0);

    // Main texture color
    vec4 texColor = getMainTextureColor(textureIsColorPresent, textureIsTexturePresent, textureAtlasNumber, pass_textureCoords, textureMaxUV, textureLayer, textureColor);

    // Diffuse color
    vec4 diffuseTexColor = getTextureColor(diffuseMapIsColorPresent, diffuseMapIsTexturePresent, diffuseMapAtlasNumber, pass_textureCoords, diffuseMapMaxUV, diffuseMapLayer, diffuseMapColor);
    vec3 diffuseColor = diffuseTexColor.xyz * light_color * light_intensity * surfaceLight;

    // Ambient color
    vec4 ambientTexColor = getTextureColor(ambientMapIsColorPresent, ambientMapIsTexturePresent, ambientMapAtlasNumber, pass_textureCoords, ambientMapMaxUV, ambientMapLayer, ambientMapColor);
    vec3 ambientColor = ambientTexColor.xyz * ambient_multiplier + base_ambient;

    // Emissive light
    vec4 emmissiveTexColor = getTextureColor(emissiveMapIsColorPresent, emissiveMapIsTexturePresent, emissiveMapAtlasNumber, pass_textureCoords, emissiveMapMaxUV, emissiveMapLayer, emissiveMapColor);
    vec3 emmissiveColor = emmissiveTexColor.xyz;

    // Specular Light
    vec4 specularTexColor = getTextureColor(specularMapIsColorPresent, specularMapIsTexturePresent, specularMapAtlasNumber, pass_textureCoords, specularMapMaxUV, specularMapLayer, specularMapColor);
    vec3 specColour = vec3(0, 0, 0);

    out_colour = vec4((diffuseColor + ambientColor + emmissiveColor + specColour) * texColor.xyz, 1);

}