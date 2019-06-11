package com.ternsip.glade.graphics.shader.impl;

import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.uniforms.UniformMatrix4;
import com.ternsip.glade.graphics.shader.uniforms.UniformTextureAddress;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChunkShader extends ShaderProgram {

    public static final File VERTEX_SHADER = new File("shaders/chunk/ChunkVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/chunk/ChunkFragmentShader.glsl");

    private final UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private final UniformMatrix4 viewMatrix = new UniformMatrix4();
    private final UniformMatrix4 transformationMatrix = new UniformMatrix4();

    private UniformTextureAddress diffuseMap = new UniformTextureAddress();
    private UniformTextureAddress specularMap = new UniformTextureAddress();
    private UniformTextureAddress ambientMap = new UniformTextureAddress();
    private UniformTextureAddress emissiveMap = new UniformTextureAddress();

}