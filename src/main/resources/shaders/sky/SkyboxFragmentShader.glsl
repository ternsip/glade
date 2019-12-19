#version 130

struct Light {
    vec3 pos;
    float intensity;
    vec3 color;
};

in  vec3 eyeVector;
out vec4 out_Color;

uniform Light sun;

void main(void) {

    float gradient = dot(normalize(sun.pos), normalize(eyeVector)) / 2.0 + 0.5;
    vec3 color = sun.intensity * vec3(pow(gradient, 32), pow(gradient, 48) / 2.0 + 0.5, gradient / 4.0 + 0.75);
    out_Color = vec4(color, 1.0);

}