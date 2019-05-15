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

vec4 getTextureColor(bool isPresent, sampler2DArray atlas, vec2 uv, vec2 maxUV, int layer, vec4 color) {
    if (isPresent) {
        return texture(atlas, vec3(uv * maxUV, layer)) * color;
    } else {
        return color;
    }
}

// TODO turn into structures, remove color present flag
void main(void){

    vec4 light_color = vec4(1, 1, 1, 1);
    vec4 light_ambient = vec4(0.5, 0.5, 0.5, 1);
    float light_intensity = 1.0;
    float diffuseFactor = 0.6;

    vec3 unitNormal = normalize(pass_normal);
    vec3 unitLight = -normalize(lightDirection);// TODO REMOVE - (minus)
    float surfaceLight = max(dot(unitLight, unitNormal), 0.0);

    vec4 texColor = getTextureColor(true, textureAtlasNumber, pass_textureCoords, textureMaxUV, textureLayer, textureColor);

    vec4 diffuseTexColor = getTextureColor(diffuseMapIsTexturePresent, diffuseMapAtlasNumber, pass_textureCoords, diffuseMapMaxUV, diffuseMapLayer, diffuseMapColor);
    vec4 diffuseColor = diffuseTexColor * light_color * light_intensity * surfaceLight;

    vec4 ambientTexColor = getTextureColor(ambientMapIsTexturePresent, ambientMapAtlasNumber, pass_textureCoords, ambientMapMaxUV, ambientMapLayer, ambientMapColor);
    vec4 ambientColor = ambientTexColor * light_ambient;

    out_colour = (diffuseColor + ambientColor) * texColor;

}