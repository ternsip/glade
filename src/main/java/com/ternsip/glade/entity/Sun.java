package com.ternsip.glade.entity;

import org.joml.Vector2f;
import org.joml.Vector3f;

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
				origin.x() + (float)Math.sin(phase * 2f * Math.PI) * size.x(),
				origin.y() + (float)Math.cos(phase * 2f * Math.PI) * size.y(),
				2000
		);
	}

	public Vector3f getColour() {
		return colour;
	}
}
