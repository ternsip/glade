package com.unifi.ing.engine.entity;



import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.unifi.ing.engine.model.TexturedModel;
import com.unifi.ing.engine.utils.Maths;

public class Entity {

	private TexturedModel model;
	private Vector3f position;
	private float rotX;
	private float rotY;
	private float rotZ;
	private float scale;

	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public static Quaternion getRotationQuat(float rotX, float rotY, float rotZ){
		float attitude = Maths.toRadians(rotX);
		float heading = Maths.toRadians(rotY);
		float bank = Maths.toRadians(rotZ);
		
		// Assuming the angles are in radians.
		float c1 = (float) Math.cos(heading);
		float s1 = (float) Math.sin(heading);
		float c2 = (float) Math.cos(attitude);
		float s2 = (float) Math.sin(attitude);
		float c3 = (float) Math.cos(bank);
		float s3 = (float) Math.sin(bank);
		float w = (float) (Math.sqrt(1.0 + c1 * c2 + c1*c3 - s1 * s2 * s3 + c2*c3) / 2.0);
		float w4 = (4.0f * w);
		float x = (c2 * s3 + c1 * s3 + s1 * s2 * c3) / w4 ;
		float y = (s1 * c2 + s1 * c3 + c1 * s2 * s3) / w4 ;
		float z = (-s1 * s3 + c1 * s2 * c3 +s2) / w4 ;
		return new Quaternion(x, y, z, w);
	}
}
