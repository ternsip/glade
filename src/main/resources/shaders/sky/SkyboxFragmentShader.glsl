#version 400

in  vec3 eyeVector;
out vec4 out_Color;

uniform vec3 sunVector;
uniform float phase;

void main(void) {

    float sunPower = (1 - abs(1/3.0 - phase) * 3);
    float moonPower = (1 - abs(0.75 - phase) * 4);;

    // Sun
    if (sunPower > 0) {
        float gradient = dot(normalize(sunVector), normalize(eyeVector)) / 2.0 + 0.5;
        out_Color = vec4(sunPower * (pow(gradient, 32)), sunPower * (pow(gradient, 48) / 2.0 + 0.5), sunPower * (gradient / 4.0 + 0.75), 1.0);
        return;
    }

    // Moon
    out_Color = vec4(0, 0, 0, 1.0);

}