package com.ternsip.glade.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import com.ternsip.glade.model.TexturedModel;
import com.ternsip.glade.utils.Maths;

@AllArgsConstructor
@Getter
@Setter
public class Entity {

	private TexturedModel model;
	private Vector3f position;
	private float rotX;
	private float rotY;
	private float rotZ;
	private float scale;

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

	public Quaternionfc getRotationQuaternion(){
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
		return new Quaternionf(x, y, z, w);
	}

    public Matrix4f getTransformationMatrix() {
	    return Maths.createTransformationMatrix(getPosition(), getRotationQuaternion(), getScale());
    }

}
