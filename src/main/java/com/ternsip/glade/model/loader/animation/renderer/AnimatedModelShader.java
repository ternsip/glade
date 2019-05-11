package com.ternsip.glade.model.loader.animation.renderer;

import com.ternsip.glade.model.loader.engine.shaders.*;

import java.io.File;

public class AnimatedModelShader extends ShaderProgram {

    private static final int MAX_JOINTS = 150;// max number of joints in a skeleton
    private static final int DIFFUSE_TEX_UNIT = 0;

    private static final File VERTEX_SHADER = new File("shaders/anim/animatedEntityVertex.glsl");
    private static final File FRAGMENT_SHADER = new File("shaders/anim/animatedEntityFragment.glsl");

    protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
    protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
    protected UniformMat4Array jointTransforms = new UniformMat4Array("jointTransforms", MAX_JOINTS);
    private UniformSampler diffuseMap = new UniformSampler("diffuseMap");

    public AnimatedModelShader() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
        super.storeAllUniformLocations(projectionViewMatrix, transformationMatrix, diffuseMap, lightDirection, jointTransforms);
        connectTextureUnits();
    }

    private void connectTextureUnits() {
        super.start();
        diffuseMap.loadTexUnit(DIFFUSE_TEX_UNIT);
        super.stop();
    }

}
