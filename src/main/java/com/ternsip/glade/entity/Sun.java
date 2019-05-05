package com.ternsip.glade.entity;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Sun implements Light {

	private float phase;
	private float delta;
	private Vector2f origin;
	private Vector2f size;
	private Vector3f colour;

	public Sun(Vector2f origin, Vector2f size, Vector3f colour) {
		this.phase = 0;
		this.delta = 0.005f;
		this.origin = origin;
		this.size = size;
		this.colour = colour;
	}

	public void move() {
		phase += delta;
	}

	public Vector3f getPosition() {
		return new Vector3f(
				origin.getX() + (float)Math.sin(phase * 2f * Math.PI) * size.getX(),
				origin.getY() + (float)Math.cos(phase * 2f * Math.PI) * size.getY(),
				2000
		);
	}

	public Vector3f getColour() {
		return colour;
	}
}
