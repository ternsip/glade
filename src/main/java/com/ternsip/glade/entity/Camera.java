package com.ternsip.glade.entity;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private float distanceFromRover = 60;
	private float angleAroundRover = 0;

	private Vector3f position = new Vector3f(0,0,0);
	private float pitch = 15;
	private float yaw;
	private float roll;

	private Rover rover;

	public Camera(Rover rover){
		this.rover = rover;
	}

	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundRover();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (rover.getRotY() + angleAroundRover);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
		float theta = rover.getRotY() + angleAroundRover;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = rover.getPosition().x - offsetX;
		position.z = rover.getPosition().z - offsetZ;
		position.y = rover.getPosition().y + verticalDistance;
	}

	private float calculateHorizontalDistance(){
		return (float) (distanceFromRover * Math.cos(Math.toRadians(pitch)));
	}


	private float calculateVerticalDistance(){
		return (float) (distanceFromRover * Math.sin(Math.toRadians(pitch)));
	}



	private void calculateZoom(){
		float zoomLevel = distanceFromRover - Mouse.getDWheel() * 0.01f;
		if(zoomLevel <= 10){
			distanceFromRover = 10;
		}else if(zoomLevel >= 200){
			distanceFromRover = 200;
		}
		else{
			distanceFromRover = zoomLevel;
		}


	}

	private void calculatePitch(){
		if(Mouse.isButtonDown(0)){
			float pitchChange = pitch - Mouse.getDY() * 0.1f;
			if(pitchChange <= -90){
				pitchChange = -90;
			}else if(pitchChange >= 90){
				pitchChange = 90;
			}else{
				pitch = pitchChange;
			}
		}
	}

	private void calculateAngleAroundRover(){
		if(Mouse.isButtonDown(0)){
			float angleChange = Mouse.getDX() * 0.1f;
			angleAroundRover -= angleChange;
		}
	}

}