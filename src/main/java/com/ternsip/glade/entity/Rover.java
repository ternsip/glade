package com.ternsip.glade.entity;

import java.util.ArrayList;
import java.util.List;

import com.ternsip.glade.observer.Observable;
import com.ternsip.glade.observer.Observer;
import org.joml.Vector3f;

import com.ternsip.glade.model.TexturedModel;
import com.ternsip.glade.terrains.MultipleTerrain;
import com.ternsip.glade.terrains.Terrain;
import com.ternsip.glade.utils.DisplayManager;
import com.ternsip.glade.utils.Maths;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static org.lwjgl.glfw.GLFW.*;

public class Rover extends Entity implements Observable {

	private static final float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160.0f;//160
	private static final float GRAVITY = -20;


	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	private float rotX = 0.60f;
	private float rotY = 0.60f;
	private float rotZ = 0.60f;

	List<Observer> observerEntity = null;


	public Rover(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		observerEntity = new ArrayList<>();
	}


	public void move(MultipleTerrain multipleTerrain){
		Terrain terrain = multipleTerrain.getTerrain(getPosition());
		checkInputs(terrain);
		super.increaseRotation(0, currentTurnSpeed * DISPLAY_MANAGER.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DISPLAY_MANAGER.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DISPLAY_MANAGER.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DISPLAY_MANAGER.getFrameTimeSeconds() , 0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y < terrainHeight){
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
		}


		notifyEntity(multipleTerrain);
		
		super.setRotZ(calcRotZ());
		super.setRotX(calcRotX());
		
	}
	
	

	private float calcRotZ(){
		Vector3f vA = ((Cube) observerEntity.get(2)).getPosition();
		Vector3f vB = ((Cube) observerEntity.get(3)).getPosition();
		
		
		Vector3f vet = new Vector3f(vB.x - vA.x, 
				vB.y - vA.y,
				vB.z - vA.z);
		Vector3f zero = new Vector3f(vet.x,0,vet.z);

		if(vet.equals(zero))
			return 0;
		
		float a = Maths.mul(vet, zero);
		float b = Maths.len(vet) * Maths.len(zero);

		if(vB.y >= vA.y)
			return (float) Maths.toDegree(Math.acos(a/b));
		
		return (float) (0 - Maths.toDegree(Math.acos(a/b)));
	}


	private float calcRotX(){
		Vector3f vA = ((Cube) observerEntity.get(0)).getPosition();
		Vector3f vB = ((Cube) observerEntity.get(1)).getPosition();
		
		
		Vector3f vet = new Vector3f(vB.x - vA.x, 
				vB.y - vA.y,
				vB.z - vA.z);
		Vector3f zero = new Vector3f(vet.x,0,vet.z);
		
		if(vet.equals(zero))
			return 0;

		float a = Maths.mul(vet, zero);
		float b = Maths.len(vet) * Maths.len(zero);

		if(vA.y >= vB.y)
			return (float) Maths.toDegree(Math.acos(a/b));
		
		return (float) (0 - Maths.toDegree(Math.acos(a/b)));
	}

	private void checkInputs(Terrain terrain){

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
	public void notifyEntity(MultipleTerrain terrain) {
		for (Observer observer : observerEntity) {
			observer.update(this, terrain);
		}
	}

	public void addObserver(Observer observer){
		observerEntity.add(observer);
	}
	
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("#################################\n");
		str.append("x:\t" + this.getPosition().x + "\n");
		str.append("y:\t" + this.getPosition().y + "\n");
		str.append("z:\t" + this.getPosition().z + "\n");
		str.append("---------------------------------\n");
		
		for (Observer observer : observerEntity) {
			Cube c = (Cube) observer;
			
			str.append(c.toString());
			
		}
		
		return str.toString();
	}
}
