#version 400 core

const int MAX_BONES = 180; // Maximal number of bones allowed in a skeleton
const int MAX_WEIGHTS = 3; // Maximal number of bones that can affect a vertex

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 pass_normal;

uniform bool animated;
uniform mat4 boneTransforms[MAX_BONES];
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void) {

    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    pass_normal = normal;
    pass_textureCoords = textureCoordinates;

}