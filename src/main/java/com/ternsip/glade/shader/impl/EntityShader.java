package com.ternsip.glade.shader.impl;

import com.ternsip.glade.shader.base.ShaderProgram;
import com.ternsip.glade.shader.uniforms.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;

import static com.ternsip.glade.universal.Mesh.MAX_BONES;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityShader extends ShaderProgram {

    public static final File VERTEX_SHADER = new File("shaders/anim/EntityVertexShader.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/anim/EntityFragmentShader.glsl");

    private UniformBoolean animated = new UniformBoolean();
    private UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private UniformMatrix4 viewMatrix = new UniformMatrix4();
    private UniformMatrix4 transformationMatrix = new UniformMatrix4();
    private UniformVec3 lightDirection = new UniformVec3();
    private UniformMatrix4Array boneTransforms = new UniformMatrix4Array(MAX_BONES);

    private UniformTextureAddress textureMap = new UniformTextureAddress();
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
