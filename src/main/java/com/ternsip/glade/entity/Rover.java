package com.ternsip.glade.entity;

import com.ternsip.glade.model.GLModel;
import org.joml.Vector3f;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static org.lwjgl.glfw.GLFW.*;

public class Rover extends Entity {

	private static final float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160.0f;
	private static final float GRAVITY = -20;


	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	private float rotX = 0.60f;
	private float rotY = 0.60f;
	private float rotZ = 0.60f;

	public Rover(GLModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}


	public void move(){
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DISPLAY_MANAGER.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DISPLAY_MANAGER.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DISPLAY_MANAGER.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DISPLAY_MANAGER.getFrameTimeSeconds() , 0);
		float terrainHeight = -5;
		if(super.getPosition().y < terrainHeight){
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
		}
		
	}

	private void checkInputs(){

		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_W)){
			this.currentSpeed = +RUN_SPEED;
		}else if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}else{
			this.currentSpeed = 0;
		}

		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
		}else{
			this.currentTurnSpeed = 0;
		}

		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_B)){
			super.increaseRotation(rotX, 0, 0);
		}
		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_N)){
			super.increaseRotation(0, rotY, 0);
		}
		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_M)){
			super.increaseRotation(0, 0, rotZ);
		}

		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_H)){
			super.increaseRotation(-rotX, 0, 0);
		}
		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_J)){
			super.increaseRotation(0, -rotY, 0);
		}
		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_K)){
			super.increaseRotation(0, 0, -rotZ);
		}

		if(DISPLAY_MANAGER.isKeyDown(GLFW_KEY_R)){
			this.setRotY(0);
			this.setRotX(0);
			this.setRotZ(0);
			this.setPosition(new Vector3f(600, 30, 550));
		}

	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("#################################\n");
		str.append("x:\t" + this.getPosition().x + "\n");
		str.append("y:\t" + this.getPosition().y + "\n");
		str.append("z:\t" + this.getPosition().z + "\n");
		str.append("---------------------------------\n");

		return str.toString();
	}
}
