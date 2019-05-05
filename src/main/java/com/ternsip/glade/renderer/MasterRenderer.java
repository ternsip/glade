package com.ternsip.glade.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.utils.DisplayManager;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.ternsip.glade.Loader;
import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Entity;
import com.ternsip.glade.model.TexturedModel;
import com.ternsip.glade.shader.model.ModelShader;
import com.ternsip.glade.shader.terrain.TerrainShader;
import com.ternsip.glade.sky.SkyRenderer;
import com.ternsip.glade.terrains.Terrain;

public class MasterRenderer {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private static final float RED = 0.823f;
	private static final float GREEN = 0.722f;
	private static final float BLUE = 0.535f;
	
	private Matrix4f projectionMatrix;

	// TODO MOVE ALL SHADERS INSIDE RENDERERS
	private ModelShader modelShader = new ModelShader();

	// TODO RENAME TO MODEL RENDERER
	private EntityRenderer entityRenderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<TexturedModel,List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private SkyRenderer skyRenderer;
	
	public MasterRenderer(Loader loader){
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(modelShader,projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
		skyRenderer = new SkyRenderer(loader, projectionMatrix);
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
        GL11.glViewport(0, 0, DisplayManager.getWidth(), DisplayManager.getHeight());
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(Sun sun, Camera camera){
		prepare();
		modelShader.start();
		modelShader.loadSkyColour(RED, GREEN, BLUE);
		modelShader.loadLight(sun);
		modelShader.loadViewMatrix(camera);
		entityRenderer.render(entities);
		modelShader.stop();
		terrainShader.start();
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadLight(sun);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		skyRenderer.render(sun, camera);
		terrains.clear();
		entities.clear();
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);		
		}
	}
	
	public void cleanUp(){
		modelShader.cleanUp();
		terrainShader.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) DisplayManager.getWidth() / (float) DisplayManager.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
	}
	

}
