#version 400 core

const vec2 lightBias = vec2(0.7, 0.6);//just indicates the balance between diffuse and ambient lighting

in vec2 pass_textureCoords;
in vec3 pass_normal;

out vec4 out_colour;

uniform bool                diffuseMapIsPresent;
uniform sampler2DArray      diffuseMapAtlasNumber;
uniform int                 diffuseMapLayer;
uniform vec2                diffuseMapMaxUV;

uniform bool                localNormalMapIsPresent;
uniform sampler2DArray      localNormalMapAtlasNumber;
uniform int                 localNormalMapLayer;
uniform vec2                localNormalMapMaxUV;

uniform bool                specularMapIsPresent;
uniform sampler2DArray      specularMapAtlasNumber;
uniform int                 specularMapLayer;
uniform vec2                specularMapMaxUV;

uniform bool                glowMapIsPresent;
uniform sampler2DArray      glowMapAtlasNumber;
uniform int                 glowMapLayer;
uniform vec2                glowMapMaxUV;

uniform int layerGlowMap;
uniform vec3 lightDirection;

void main(void){

    vec4 resultColour = vec4(1, 1, 1, 1);

    if (diffuseMapIsPresent) {
        resultColour = texture(diffuseMapAtlasNumber, vec3(pass_textureCoords*diffuseMapMaxUV, diffuseMapLayer));
    }

    if (localNormalMapIsPresent) {
        //resultColour = texture(localNormalMapAtlasNumber, vec3(pass_textureCoords*localNormalMapMaxUV, layerDiffuseMap));
    }

    if (specularMapIsPresent) {
        //resultColour = texture(specularMapAtlasNumber, vec3(pass_textureCoords*specularMapMaxUV, specularMapLayer));
    }

    if (glowMapIsPresent) {
        //resultColour = texture(glowMapAtlasNumber, vec3(pass_textureCoords*glowMapMaxUV, glowMapLayer));
    }

    vec3 unitNormal = normalize(pass_normal);
    float diffuseLight = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
    out_colour = resultColour * diffuseLight;

}