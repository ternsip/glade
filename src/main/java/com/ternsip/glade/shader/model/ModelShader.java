package com.ternsip.glade.shader.model;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Light;
import com.ternsip.glade.shader.ShaderProgram;
import com.ternsip.glade.utils.Maths;
import com.ternsip.glade.utils.ShaderPath;

import java.io.File;


public class ModelShader extends ShaderProgram{
	
//	Questa classe estende ShaderProgram viene principalmente utilizzata sia per gli oggetti statici si a per gli oggetti dinamici come il Rover
//	Consente di caricare lo shader fornendo i valori di tutti i parametri utilizzati per il calcolo delle texture e dei vertici
	
	private static final File VERTEX_FILE = new File("shaders/model/vertexShader.txt");
	private static final File FRAGMENT_FILE = new File("shaders/model/fragmentShader.txt");
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_SkyColour;

	public ModelShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColour = super.getUniformLocation("lightColour");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_SkyColour = super.getUniformLocation("skyColour");
		
	}
	
	public void loadSkyColour(float r, float g, float b){
		super.loadVector(location_SkyColour, new Vector3f(r,g,b));
	}
	
	public void loadFakeLightingVariable(boolean useFake){
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	
	public void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadLight(Light light){
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColour, light.getColour());
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	

}
