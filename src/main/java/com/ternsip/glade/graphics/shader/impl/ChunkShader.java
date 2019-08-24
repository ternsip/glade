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

    public static final File VERTEX_SHADER = new File("shaders/chunk/ChunkVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/chunk/ChunkFragmentShader.glsl");

    public static final AttributeData TEXTURES = new AttributeData(2, "textureCoordinates", 2, AttributeData.ArrayType.FLOAT);
    public static final AttributeData TEXTURES_START = new AttributeData(3, "textureStart", 2, AttributeData.ArrayType.FLOAT);
    public static final AttributeData TEXTURES_END = new AttributeData(4, "textureEnd", 2, AttributeData.ArrayType.FLOAT);
    public static final AttributeData SKY_LIGHT = new AttributeData(5, "skyLight", 1, AttributeData.ArrayType.FLOAT);
    public static final AttributeData EMIT_LIGHT = new AttributeData(6, "emitLight", 1, AttributeData.ArrayType.FLOAT);
    public static final AttributeData NORMALS = new AttributeData(7, "normal", 3, AttributeData.ArrayType.FLOAT);
    public static final AttributeData BLOCK_TYPE = new AttributeData(8, "blockType", 1, AttributeData.ArrayType.FLOAT);

    private final UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private final UniformMatrix4 viewMatrix = new UniformMatrix4();
    private final UniformMatrix4 transformationMatrix = new UniformMatrix4();

    private final UniformLight sun = new UniformLight();
    private final UniformFloat time = new UniformFloat();
    private final UniformTextureAddress diffuseMap = new UniformTextureAddress();

}