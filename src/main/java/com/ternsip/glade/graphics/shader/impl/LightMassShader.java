package com.ternsip.glade.graphics.shader.impl;

import com.ternsip.glade.graphics.shader.base.BufferLayout;
import com.ternsip.glade.graphics.shader.base.ComputeShader;
import com.ternsip.glade.graphics.shader.uniforms.UniformBoolean;
import com.ternsip.glade.graphics.shader.uniforms.UniformInteger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LightMassShader extends ComputeShader {

    public static final File COMPUTE_SHADER = new File("shaders/chunk/LightMassShader.glsl");

    private final BufferLayout lightBuffer = new BufferLayout();
    private final BufferLayout engagedBlockIndexBuffer = new BufferLayout();
    private final BufferLayout engagedBlockBuffer = new BufferLayout();
    private final BufferLayout heightBuffer = new BufferLayout();

    private final UniformInteger startX  = new UniformInteger();
    private final UniformInteger startY  = new UniformInteger();
    private final UniformInteger startZ  = new UniformInteger();
    private final UniformInteger sizeX  = new UniformInteger();
    private final UniformInteger sizeY  = new UniformInteger();
    private final UniformInteger sizeZ  = new UniformInteger();
    private final UniformBoolean disableStartX = new UniformBoolean();
    private final UniformBoolean disableStartY = new UniformBoolean();
    private final UniformBoolean disableStartZ = new UniformBoolean();
    private final UniformBoolean disableEndX = new UniformBoolean();
    private final UniformBoolean disableEndY = new UniformBoolean();
    private final UniformBoolean disableEndZ = new UniformBoolean();

}