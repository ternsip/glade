#version 400 core

const vec2 lightBias = vec2(0.7, 0.6);//just indicates the balance between diffuse and ambient lighting

in vec2 pass_textureCoords;
in vec3 pass_normal;

out vec4 out_colour;

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

uniform int layerGlowMap;
uniform vec3 lightDirection;

void main(void){

    vec4 resultColour = vec4(1, 1, 1, 1);

    if (diffuseMapIsTexturePresent) {
        resultColour = texture(diffuseMapAtlasNumber, vec3(pass_textureCoords*diffuseMapMaxUV, diffuseMapLayer));
    }

    vec3 unitNormal = normalize(pass_normal);
    float diffuseLight = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
    out_colour = resultColour * diffuseLight;

}