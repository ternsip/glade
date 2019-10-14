#version 430 core

struct Light {
    vec3 pos;
    float intensity;
    vec3 color;
};

#define M_PI 3.1415926535897932384626433832795
const int MAX_SAMPLERS = 16;
const int BLOCK_TYPE_WATER = 1;

in vec2 passTextureCoords;
in vec3 passWorldPos;
in float passAtlasNumber;
in float passAtlasLayer;
in vec2 passAtlasMaxUV;
in float passAmbient;
in vec3 passNormal;
in float passBlockType;
in float visibility;
in float passSkyLight;
in float passEmitLight;

out vec4 out_colour;

uniform float time;
uniform sampler2DArray[MAX_SAMPLERS] samplers;
uniform vec3 fogColor;
uniform Light sun;

bool isBlockOfType(int type) {
    return abs(passBlockType - type) < 1e-3;
}

int roundFloat(float value) {
    return int(round(value));
}

void main(void){

    float surfaceLight = max(dot(normalize(sun.pos), normalize(passNormal)), 0.0);
    float passAmbient = min(1, max(sun.intensity * passSkyLight, passEmitLight));

    if (isBlockOfType(BLOCK_TYPE_WATER)) {
        vec2 cPos = -1.0 + 2.0 * passWorldPos.xz;
        float cLength = length(cPos);
        vec2 uv = passWorldPos.xz + (cPos / cLength) * cos(cLength * 12.0 - M_PI * 2 * time * 1) * 0.03;
        uv = abs(vec2(mod(uv.x, 1.0), mod(uv.y, 1.0)));
        vec3 resultColor = passAmbient * texture(samplers[roundFloat(passAtlasNumber)], vec3(uv * passAtlasMaxUV, roundFloat(passAtlasLayer))).xyz;
        out_colour = vec4(passAmbient * mix(fogColor, resultColor, visibility), 0.8);
        return;
    }

    vec4 tex = texture(samplers[roundFloat(passAtlasNumber)], vec3(passTextureCoords * passAtlasMaxUV, roundFloat(passAtlasLayer)));
    out_colour = vec4(passAmbient * mix(fogColor, tex.xyz, visibility), tex.a);

}