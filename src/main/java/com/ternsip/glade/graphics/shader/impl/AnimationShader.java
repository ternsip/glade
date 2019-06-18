package com.ternsip.glade.graphics.shader.impl;

import com.ternsip.glade.graphics.shader.base.AttributeData;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.uniforms.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;

import static com.ternsip.glade.graphics.shader.uniforms.UniformLightArray.MAX_LIGHTS;

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

    private UniformBoolean animated = new UniformBoolean();
    private UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private UniformMatrix4 viewMatrix = new UniformMatrix4();
    private UniformMatrix4 transformationMatrix = new UniformMatrix4();
    private UniformLightArray lights = new UniformLightArray(MAX_LIGHTS);
    private UniformMatrix4Array boneTransforms = new UniformMatrix4Array(MAX_BONES);

    private UniformTextureAddress diffuseMap = new UniformTextureAddress();
    private UniformTextureAddress specularMap = new UniformTextureAddress();
    private UniformTextureAddress ambientMap = new UniformTextureAddress();
    private UniformTextureAddress emissiveMap = new UniformTextureAddress();
    private UniformTextureAddress heightMap = new UniformTextureAddress();
    private UniformTextureAddress normalsMap = new UniformTextureAddress();
    private UniformTextureAddress shininessMap = new UniformTextureAddress();
    private UniformTextureAddress opacityMap = new UniformTextureAddress();
    private UniformTextureAddress displacementMap = new UniformTextureAddress();
    private UniformTextureAddress lightMap = new UniformTextureAddress();
    private UniformTextureAddress reflectionMap = new UniformTextureAddress();

}
