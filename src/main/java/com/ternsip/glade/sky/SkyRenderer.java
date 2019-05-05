package com.ternsip.glade.sky;

import com.ternsip.glade.Loader;
import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.RawModel;
import com.ternsip.glade.shader.sky.SkyboxShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class SkyRenderer {

    public static final Vector3f SKY_COLOR = new Vector3f(0.823f, 0.722f,  0.535f);

	private static final float SIZE = 500f;

	private static final float[] VERTICES = {
			-SIZE,  SIZE, -SIZE,
			-SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,

			-SIZE, -SIZE,  SIZE,
			-SIZE, -SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE,  SIZE,
			-SIZE, -SIZE,  SIZE,

			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,

			-SIZE, -SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE, -SIZE,  SIZE,
			-SIZE, -SIZE,  SIZE,

			-SIZE,  SIZE, -SIZE,
			SIZE,  SIZE, -SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE, -SIZE,

			-SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			SIZE, -SIZE,  SIZE
	};

	private RawModel skyBox;
	private SkyboxShader skyboxShader;
	
	public SkyRenderer(Loader loader, Matrix4f projectionMatrix){
		skyBox = loader.loadToVAO(VERTICES, 3);
		skyboxShader = new SkyboxShader();
		skyboxShader.start();
		skyboxShader.loadProjectionMatrix(projectionMatrix);
		skyboxShader.stop();
	}
	
	public void render(Sun sun, Camera camera){
		skyboxShader.start();
		skyboxShader.loadSunVector(sun.getPosition());
		skyboxShader.loadViewMatrix(camera);
		GL30.glBindVertexArray(skyBox.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, skyBox.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		skyboxShader.stop();
	}

    public void cleanUp() {
        skyboxShader.cleanUp();
    }

}
