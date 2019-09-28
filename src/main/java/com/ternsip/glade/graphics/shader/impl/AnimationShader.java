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
public final class AnimationShader extends ShaderProgram {

    public static final int MAX_WEIGHTS = 4;
    public static final int MAX_BONES = 180;

    public static final File VERTEX_SHADER = new File("shaders/animation/AnimationVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/animation/AnimationFragmentShader.glsl");

    public static final AttributeData TEXTURES = new AttributeData(2, "textureCoordinates", 2, AttributeData.ArrayType.FLOAT);
    public static final AttributeData COLORS = new AttributeData(3, "colors", 4, AttributeData.ArrayType.FLOAT);
    public static final AttributeData NORMALS = new AttributeData(4, "normal", 3, AttributeData.ArrayType.FLOAT);
    public static final AttributeData BONE_INDICES = new AttributeData(5, "boneIndices", MAX_WEIGHTS, AttributeData.ArrayType.INT);
    public static final AttributeData WEIGHTS = new AttributeData(6, "weights", MAX_WEIGHTS, AttributeData.ArrayType.FLOAT);

    private final UniformBoolean animated = new UniformBoolean();
    private final UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private final UniformMatrix4 viewMatrix = new UniformMatrix4();
    private final UniformMatrix4 transformationMatrix = new UniformMatrix4();
    private final UniformLight sun = new UniformLight();
    private final UniformMatrix4Array boneTransforms = new UniformMatrix4Array(MAX_BONES);

    private final UniformTextureAddress diffuseMap = new UniformTextureAddress();
    private final UniformTextureAddress specularMap = new UniformTextureAddress();
    private final UniformTextureAddress ambientMap = new UniformTextureAddress();
    private final UniformTextureAddress emissiveMap = new UniformTextureAddress();
    private final UniformTextureAddress heightMap = new UniformTextureAddress();
    private final UniformTextureAddress normalsMap = new UniformTextureAddress();
    private final UniformTextureAddress shininessMap = new UniformTextureAddress();
    private final UniformTextureAddress opacityMap = new UniformTextureAddress();
    private final UniformTextureAddress displacementMap = new UniformTextureAddress();
    private final UniformTextureAddress lightMap = new UniformTextureAddress();
    private final UniformTextureAddress reflectionMap = new UniformTextureAddress();

    private final UniformVec3 fogColor = new UniformVec3();
    private final UniformFloat fogDensity = new UniformFloat();
    private final UniformFloat fogGradient = new UniformFloat();

}
