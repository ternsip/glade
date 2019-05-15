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
public final class AnimatedModelShader extends ShaderProgram {

    public static final File VERTEX_SHADER = new File("shaders/anim/animatedEntityVertex.glsl");
    public static final File FRAGMENT_SHADER = new File("shaders/anim/animatedEntityFragment.glsl");

    private UniformBoolean animated = new UniformBoolean();
    private UniformMatrix4 projectionMatrix = new UniformMatrix4();
    private UniformMatrix4 viewMatrix = new UniformMatrix4();
    private UniformMatrix4 transformationMatrix = new UniformMatrix4();
    private UniformVec3 lightDirection = new UniformVec3();
    private UniformMatrix4Array boneTransforms = new UniformMatrix4Array(MAX_BONES);
    private UniformTextureAddress diffuseMap = new UniformTextureAddress();
    private UniformTextureAddress localNormalMap = new UniformTextureAddress();
    private UniformTextureAddress specularMap = new UniformTextureAddress();
    private UniformTextureAddress glowMap = new UniformTextureAddress();

}
