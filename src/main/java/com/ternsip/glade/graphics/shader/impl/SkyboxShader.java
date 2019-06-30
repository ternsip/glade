package com.ternsip.glade.graphics.shader.impl;

import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.uniforms.UniformFloat;
import com.ternsip.glade.graphics.shader.uniforms.UniformMatrix4;
import com.ternsip.glade.graphics.shader.uniforms.UniformVec3;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SkyboxShader extends ShaderProgram {

    public static final File VERTEX_SHADER = new File("shaders/sky/SkyboxVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/sky/SkyboxFragmentShader.glsl");

    private final UniformFloat phase = new UniformFloat();
    private final UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private final UniformMatrix4 viewMatrix = new UniformMatrix4();
    private final UniformVec3 sunVector = new UniformVec3();

}