#version 400 core

struct Light {
    vec3 pos;
    float intensity;
    vec3 color;
};

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in float skyLight;
in float emitLight;

out vec2 pass_textureCoords;
out vec3 pass_normal;
out float pass_ambient;

uniform Light sun;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void) {

    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    pass_normal =  (projectionMatrix * transformationMatrix * vec4(normal, 0.0)).xyz;
    pass_ambient = min(1, sun.intensity * skyLight + emitLight);
    pass_textureCoords = textureCoordinates;

}