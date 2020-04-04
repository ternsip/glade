package com.ternsip.glade.graphics.shader.impl;

import com.ternsip.glade.graphics.shader.base.RasterShader;
import com.ternsip.glade.graphics.shader.uniforms.UniformLight;
import com.ternsip.glade.graphics.shader.uniforms.UniformMatrix4;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SkyboxShader extends RasterShader {

    public static final File VERTEX_SHADER = new File("shaders/sky/SkyboxVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/sky/SkyboxFragmentShader.glsl");

    private final UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private final UniformMatrix4 viewMatrix = new UniformMatrix4();
    private final UniformLight sun = new UniformLight();

}