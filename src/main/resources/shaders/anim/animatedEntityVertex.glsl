#version 150

const int MAX_BONES = 180; // Maximal number of bones allowed in a skeleton
const int MAX_WEIGHTS = 3; // Maximal number of bones that can affect a vertex

in vec3 in_position;
in vec2 in_textureCoords;
in vec3 in_normal;
in ivec3 in_boneIndices;
in vec3 in_weights;

out vec2 pass_textureCoords;
out vec3 pass_normal;

uniform bool animated;
uniform mat4 boneTransforms[MAX_BONES];
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void) {

    vec4 totalLocalPos = vec4(in_position, 1.0);
    vec4 totalNormal = vec4(in_normal, 0.0);

    if (animated) {
        totalLocalPos = vec4(0.0);
        totalNormal = vec4(0.0);
        for (int i = 0; i < MAX_WEIGHTS; i++){
            mat4 boneTransform = boneTransforms[in_boneIndices[i]];
            vec4 posePosition = boneTransform * vec4(in_position, 1.0);
            totalLocalPos += posePosition * in_weights[i];
            vec4 worldNormal = boneTransform * vec4(in_normal, 0.0);
            totalNormal += worldNormal * in_weights[i];
        }
    }

    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * totalLocalPos;
    pass_normal = totalNormal.xyz;
    pass_textureCoords = in_textureCoords;

}