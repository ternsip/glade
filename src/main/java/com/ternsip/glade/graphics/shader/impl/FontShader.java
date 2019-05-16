package com.ternsip.glade.graphics.shader.impl;

import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.uniforms.UniformMatrix4;
import com.ternsip.glade.graphics.shader.uniforms.UniformVec3;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FontShader extends ShaderProgram {

    public static final File VERTEX_SHADER = new File("shaders/font/FontVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/font/FontFragmentShader.glsl");


}