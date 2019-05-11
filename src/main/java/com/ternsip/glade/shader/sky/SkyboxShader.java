package com.ternsip.glade.shader.sky;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;

import static com.ternsip.glade.model.Mesh.VERTICES_ATTRIBUTE_POINTER_INDEX;


public class SkyboxShader extends ShaderProgram {

    private static final File VERTEX_FILE = new File("shaders/sky/skyboxVertexShader.txt");
    private static final File FRAGMENT_FILE = new File("shaders/sky/skyboxFragmentShader.txt");

    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_sunVector;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f matrix = camera.createViewMatrix();
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
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
        super.bindAttribute(VERTICES_ATTRIBUTE_POINTER_INDEX, "position");
    }

}