#version 130

// Maximal number of bones allowed in a skeleton, not all of them should be loaded into uniform
const int MAX_BONES = 180;

// Maximal number of bones that can affect a vertex, that affects bone indices and weights vector length
const int MAX_WEIGHTS = 4;

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in ivec4 boneIndices;
in vec4 weights;

out vec2 pass_textureCoords;
out vec3 pass_normal;
out float visibility;

uniform bool animated;
uniform mat4 boneTransforms[MAX_BONES];
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;
uniform float fogDensity;
uniform float fogGradient;

mat4 getSkinMatrix() {
    if (!animated) {
        return mat4(1);
    }
    mat4 mat = mat4(0);
    for (int i = 0; i < MAX_WEIGHTS; i++){
        mat += weights[i] * boneTransforms[boneIndices[i]];
    }
    return mat;
}

void main(void) {

    mat4 skinMat = getSkinMatrix();

    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * skinMat * vec4(position, 1.0);
    pass_normal =  (projectionMatrix * transformationMatrix * skinMat * vec4(normal, 0.0)).xyz;
    pass_textureCoords = textureCoordinates;
    float distance_to_cam = length(viewMatrix * transformationMatrix * skinMat * vec4(position, 1.0));
    visibility = clamp(exp(-pow(distance_to_cam * fogDensity, fogGradient)), 0, 1);

}