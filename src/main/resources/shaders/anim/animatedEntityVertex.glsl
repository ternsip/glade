#version 150

const int MAX_BONES = 180; // Maximal number of bones allowed in a skeleton
const int MAX_WEIGHTS = 3; // Maximal number of bones that can affect a vertex

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in ivec3 boneIndices;
in vec3 weights;

out vec2 pass_textureCoords;
out vec3 pass_normal;

uniform bool animated;
uniform mat4 boneTransforms[MAX_BONES];
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void) {

    vec4 totalLocalPos = vec4(position, 1.0);
    vec4 totalNormal = vec4(normal, 0.0);

    if (animated) {
        totalLocalPos = vec4(0.0);
        totalNormal = vec4(0.0);
        for (int i = 0; i < MAX_WEIGHTS; i++){
            mat4 boneTransform = boneTransforms[boneIndices[i]];
            vec4 posePosition = boneTransform * vec4(position, 1.0);
            totalLocalPos += posePosition * weights[i];
            vec4 worldNormal = boneTransform * vec4(normal, 0.0);
            totalNormal += worldNormal * weights[i];
        }
    }

    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * totalLocalPos;
    pass_normal = totalNormal.xyz;
    pass_textureCoords = textureCoordinates;

}