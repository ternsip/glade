#version 130

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

vec4 getTexture(int index, vec3 pos) {
    if (index == 0) return texture(samplers[0], pos);
    else if (index == 1) return texture(samplers[1], pos);
    else if (index == 2) return texture(samplers[2], pos);
    else if (index == 3) return texture(samplers[3], pos);
    else if (index == 4) return texture(samplers[4], pos);
    else if (index == 5) return texture(samplers[5], pos);
    else if (index == 6) return texture(samplers[6], pos);
    else if (index == 7) return texture(samplers[7], pos);
    else if (index == 8) return texture(samplers[8], pos);
    else if (index == 9) return texture(samplers[9], pos);
    else if (index == 10) return texture(samplers[10], pos);
    else if (index == 11) return texture(samplers[11], pos);
    else if (index == 12) return texture(samplers[12], pos);
    else if (index == 14) return texture(samplers[14], pos);
    return texture(samplers[15], pos);
}

void main(void){

    float surfaceLight = max(dot(normalize(sun.pos), normalize(passNormal)), 0.0);
    float passAmbient = min(1, max(sun.intensity * passSkyLight, passEmitLight));
    int atlasNumber = roundFloat(passAtlasNumber);

    if (isBlockOfType(BLOCK_TYPE_WATER)) {
        vec2 cPos = -1.0 + 2.0 * passWorldPos.xz;
        float cLength = length(cPos);
        vec2 uv = passWorldPos.xz + (cPos / cLength) * cos(cLength * 12.0 - M_PI * 2 * time * 1) * 0.03;
        uv = abs(vec2(mod(uv.x, 1.0), mod(uv.y, 1.0)));
        vec3 resultColor = passAmbient * getTexture(atlasNumber, vec3(uv * passAtlasMaxUV, roundFloat(passAtlasLayer))).xyz;
        out_colour = vec4(passAmbient * mix(fogColor, resultColor, visibility), 0.8);
        return;
    }

    vec4 tex = getTexture(atlasNumber, vec3(passTextureCoords * passAtlasMaxUV, roundFloat(passAtlasLayer)));
    out_colour = vec4(passAmbient * mix(fogColor, tex.xyz, visibility), tex.a);

}