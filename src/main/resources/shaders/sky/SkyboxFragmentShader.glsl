#version 400

in  vec3 eyeVector;
out vec4 out_Color;

uniform vec3 sunVector;

void main(void){
    float gradient = dot(normalize(sunVector), normalize(eyeVector)) / 2.0 + 0.5;
    out_Color = vec4(pow(gradient, 32), pow(gradient, 48) / 2.0 + 0.5, gradient / 4.0 + 0.75, 1.0);
}