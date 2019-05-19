package com.ternsip.glade.graphics.shader.impl;

import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpriteShader extends ShaderProgram {

    public static final File VERTEX_SHADER = new File("shaders/font/SpriteVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/font/SpriteFragmentShader.glsl");


}