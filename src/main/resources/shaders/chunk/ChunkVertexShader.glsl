#version 400 core

struct Light {
    vec3 pos;
    float intensity;
    vec3 color;
};

in vec3 position;
in vec2 textureCoordinates;
in vec2 textureStart;
in vec2 textureEnd;
in vec3 normal;
in float skyLight;
in float emitLight;
in float blockType;

out vec2 passTextureCoords;
out vec2 passTextureStart;
out vec2 passTextureEnd;
out vec3 passNormal;
out float passAmbient;
out float blockTypeValue;

uniform Light sun;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void) {

    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    passNormal =  (projectionMatrix * transformationMatrix * vec4(normal, 0.0)).xyz;
    passAmbient = min(1, sun.intensity * skyLight + emitLight);
    passTextureCoords = textureCoordinates;
    passTextureStart = textureStart;
    passTextureEnd = textureEnd;
    blockTypeValue = blockType;

}