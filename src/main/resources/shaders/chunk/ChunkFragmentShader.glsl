#version 400 core

#define M_PI 3.1415926535897932384626433832795
const int BLOCK_TYPE_WATER = 1;

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
in float blockTypeValue;

out vec4 out_colour;

uniform float time;
uniform bool water;
uniform vec2 waterTextureStart;
uniform vec2 waterTextureEnd;
uniform TextureData diffuseMap;

bool isBlockOfType(int type) {
    return abs(blockTypeValue - type) < 1e-3;
}

void main(void){

    if (isBlockOfType(BLOCK_TYPE_WATER)) {
        vec2 diff = waterTextureEnd - waterTextureStart;
        vec2 cur = (pass_textureCoords - waterTextureStart) / diff;
        float cc = 400;
        vec2 cPos = -1.0 + 2.0 * gl_FragCoord.xy / cc;
        float cLength = length(cPos);
        vec2 uv = cur * (gl_FragCoord.xy / cc + (cPos / cLength) * cos(cLength * 12.0 - M_PI * 2 * time * 1) * 0.03);
        uv = waterTextureStart + abs(vec2(mod(uv.x, 1.0), mod(uv.y, 1.0))) * diff;
        out_colour = vec4(pass_ambient * texture(diffuseMap.atlasNumber, vec3(uv * diffuseMap.maxUV, diffuseMap.layer)).xyz, 0.8);
        return;
    }

    vec3 base_ambient = vec3(pass_ambient, pass_ambient, pass_ambient);
    vec4 tex = texture(diffuseMap.atlasNumber, vec3(pass_textureCoords * diffuseMap.maxUV, diffuseMap.layer));
    out_colour = vec4(tex.xyz * base_ambient, tex.a);

}