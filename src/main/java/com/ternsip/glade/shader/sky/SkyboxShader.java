package com.unifi.ing.engine.shader.sky;

import org.lwjgl.util.vector.Matrix4f;

import com.unifi.ing.engine.entity.Camera;
import com.unifi.ing.engine.shader.ShaderProgram;
import com.unifi.ing.engine.utils.Maths;
import com.unifi.ing.engine.utils.ShaderPath;
import org.lwjgl.util.vector.Vector3f;


public class SkyboxShader extends ShaderProgram{
 
    private static final String VERTEX_FILE = ShaderPath.getShaderPath("sky/skyboxVertexShader.txt");
    private static final String FRAGMENT_FILE = ShaderPath.getShaderPath("sky/skyboxFragmentShader.txt");
     
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_sunVector;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }
 
    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        super.loadMatrix(location_viewMatrix, matrix);
    }

    public void loadSunVector(Vector3f position) {
        super.loadVector(location_sunVector, position);
    }
     
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_sunVector = super.getUniformLocation("sunVector");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
 
}