#version 400 core

#define M_PI 3.1415926535897932384626433832795

struct TextureData {
    bool isTexturePresent;
    bool isColorPresent;
    vec4 color;
    sampler2DArray atlasNumber;
    int layer;
    vec2 maxUV;
};

in vec2 pass_textureCoords;
in float pass_ambient;
in vec3 pass_normal;

out vec4 out_colour;

uniform float time;
uniform bool water;
uniform vec2 waterTextureStart;
uniform vec2 waterTextureEnd;
uniform TextureData diffuseMap;
uniform TextureData specularMap;
uniform TextureData ambientMap;
uniform TextureData emissiveMap;

vec4 getTextureColor(TextureData textureData, bool mainTexture) {
    bool force = !textureData.isColorPresent && !textureData.isTexturePresent && mainTexture;
    if (force || textureData.isTexturePresent) {
        vec4 texel = texture(textureData.atlasNumber, vec3(pass_textureCoords * textureData.maxUV, textureData.layer));
        return (force || textureData.isColorPresent) ? (texel * textureData.color) : texel;
    }
    return textureData.isColorPresent ? textureData.color : vec4(0, 0, 0, 0);
}

void main(void){

    if (water) {
        vec2 diff = waterTextureEnd - waterTextureStart;
        vec2 cur = (pass_textureCoords - waterTextureStart) / diff;
        float cc = 400;
        vec2 cPos = -1.0 + 2.0 * gl_FragCoord.xy / cc;
        float cLength = length(cPos);
        vec2 uv = cur * (gl_FragCoord.xy / cc + (cPos / cLength) * cos(cLength * 12.0 - M_PI * 2 * time * 1) * 0.03);
        uv = waterTextureStart + abs(vec2(mod(uv.x, 1.0), mod(uv.y, 1.0))) * diff;
        out_colour = texture(diffuseMap.atlasNumber, vec3(uv * diffuseMap.maxUV, diffuseMap.layer));
        return;
    }

    vec3 base_ambient = vec3(pass_ambient, pass_ambient, pass_ambient);
    float ambient_multiplier = 0.5;
    float diffuseFactor = 0.6;

    vec3 unitNormal = normalize(pass_normal);

    // Diffuse color
    vec4 texColor = getTextureColor(diffuseMap, true);

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
    vec3 specColour = specularTexColor.xyz;

    out_colour = vec4((ambientColor + emmissiveColor + specColour) * texColor.xyz, texColor.a);

}