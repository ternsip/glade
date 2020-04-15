#version 430 core

const float MAX_LIGHT_LEVEL = 15.0;

in vec3 position;
in vec2 textureCoordinates;
in float atlasNumber;
in float atlasLayer;
in vec2 atlasMaxUV;
in vec2 textureStart;
in vec2 textureEnd;
in vec3 normal;
in float blockType;
in float skyLight;
in float emitLight;

out vec2 passTextureCoords;
out vec3 passWorldPos;
out float passAtlasNumber;
out float passAtlasLayer;
out vec2 passAtlasMaxUV;
out vec3 passNormal;
out float passAmbient;
out float passBlockType;
out float visibility;
out float passSkyLight;
out float passEmitLight;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;
uniform float fogDensity;
uniform float fogGradient;

void main(void) {

    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    passNormal =  (projectionMatrix * transformationMatrix * vec4(normal, 0.0)).xyz;
    passTextureCoords = textureCoordinates;
    passAtlasNumber = atlasNumber;
    passAtlasLayer = atlasLayer;
    passAtlasMaxUV = atlasMaxUV;
    passBlockType = blockType;
    float distance_to_cam = length(viewMatrix * transformationMatrix * vec4(position, 1.0));
    visibility = clamp(exp(-pow(distance_to_cam * fogDensity, fogGradient)), 0, 1);
    passWorldPos = (transformationMatrix * vec4(position, 1.0)).xyz;
    passSkyLight = skyLight;
    passEmitLight = emitLight;

}