package com.ternsip.glade.graphics.shader.impl;

import com.ternsip.glade.graphics.shader.base.AttributeData;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.uniforms.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChunkShader extends ShaderProgram {

    public static final int MAX_SAMPLERS = 16;

    public static final File VERTEX_SHADER = new File("shaders/chunk/ChunkVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/chunk/ChunkFragmentShader.glsl");

    public static final AttributeData TEXTURES = new AttributeData(2, "textureCoordinates", 2, AttributeData.ArrayType.FLOAT);
    public static final AttributeData ATLAS_NUMBER = new AttributeData(3, "atlasNumber", 1, AttributeData.ArrayType.FLOAT);
    public static final AttributeData ATLAS_LAYER = new AttributeData(4, "atlasLayer", 1, AttributeData.ArrayType.FLOAT);
    public static final AttributeData ATLAS_MAX_UV = new AttributeData(5, "atlasMaxUV", 2, AttributeData.ArrayType.FLOAT);
    public static final AttributeData SKY_LIGHT = new AttributeData(6, "skyLight", 1, AttributeData.ArrayType.FLOAT);
    public static final AttributeData EMIT_LIGHT = new AttributeData(7, "emitLight", 1, AttributeData.ArrayType.FLOAT);
    public static final AttributeData NORMALS = new AttributeData(8, "normal", 3, AttributeData.ArrayType.FLOAT);
    public static final AttributeData BLOCK_TYPE = new AttributeData(9, "blockType", 1, AttributeData.ArrayType.FLOAT);

    private final UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private final UniformMatrix4 viewMatrix = new UniformMatrix4();
    private final UniformMatrix4 transformationMatrix = new UniformMatrix4();

    private final UniformLight sun = new UniformLight();
    private final UniformFloat time = new UniformFloat();
    private final UniformSamplers2DArray samplers = new UniformSamplers2DArray(MAX_SAMPLERS);
    private final UniformVec3 fogColor = new UniformVec3();
    private final UniformFloat fogDensity = new UniformFloat();
    private final UniformFloat fogGradient = new UniformFloat();

}