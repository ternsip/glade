package com.unifi.ing.engine.utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.unifi.ing.engine.entity.Camera;

public class Maths {

	public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
	public static final float PI = 3.1415927f;
	public static final float PI2 = PI * 2;
	public static final float radiansToDegrees = 180f / PI;
	public static final float degreesToRadians = PI / 180;
	public static final float PI_OVER_180 = PI / 180.0f;
	public static final float PI_UNDER_180 = 180.0f / PI;



	public static Matrix4f createTransformationMatrix(Vector3f translation , float rx, float ry, float rz , float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation,matrix,matrix);
		Matrix4f.rotate((float) Math.toRadians(rx),new Vector3f(1, 0, 0) , matrix,matrix);
		Matrix4f.rotate((float) Math.toRadians(ry),new Vector3f(0, 1, 0) , matrix,matrix);
		Matrix4f.rotate((float) Math.toRadians(rz),new Vector3f(0, 0, 1) , matrix,matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}


	public static Matrix4f createTransformationMatrix(Vector3f translation, Quaternion rotation, float scale){
		Matrix4f matrix = new Matrix4f();

		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.mul(matrix, convertQuaternionToMatrix4f(rotation), matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rot, float scale){
		Matrix4f matrix = new Matrix4f();

		Quaternion roll = new Quaternion();
		roll.setFromAxisAngle(new Vector4f(0f, 0f, -1f, rot.z * 90F * degreesToRadians));
		roll.normalise();

		Quaternion pitch = new Quaternion();
		pitch.setFromAxisAngle(new Vector4f(-1f, 0f, 0f, rot.x * 90F * degreesToRadians));
		pitch.normalise();

		Quaternion yaw = new Quaternion();
		yaw.setFromAxisAngle(new Vector4f(0f, 1f, 0f, rot.y * 90F * degreesToRadians));
		yaw.normalise();

		Quaternion rotation = Quaternion.mul(Quaternion.mul(roll, pitch, null), yaw, null);

		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.mul(matrix, convertQuaternionToMatrix4f(rotation), matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	private static Matrix4f convertQuaternionToMatrix4f(Quaternion q){
		Matrix4f matrix = new Matrix4f();

		matrix.m00 = 1.0f - 2.0f * ( q.getY() * q.getY() + q.getZ() * q.getZ() );
		matrix.m01 = 2.0f * (q.getX() * q.getY() + q.getZ() * q.getW());
		matrix.m02 = 2.0f * (q.getX() * q.getZ() - q.getY() * q.getW());
		matrix.m03 = 0.0f;

		// Second row
		matrix.m10 = 2.0f * ( q.getX() * q.getY() - q.getZ() * q.getW() );
		matrix.m11 = 1.0f - 2.0f * ( q.getX() * q.getX() + q.getZ() * q.getZ() );
		matrix.m12 = 2.0f * (q.getZ() * q.getY() + q.getX() * q.getW() );
		matrix.m13 = 0.0f;

		// Third row
		matrix.m20 = 2.0f * ( q.getX() * q.getZ() + q.getY() * q.getW() );
		matrix.m21 = 2.0f * ( q.getY() * q.getZ() - q.getX() * q.getW() );
		matrix.m22 = 1.0f - 2.0f * ( q.getX() * q.getX() + q.getY() * q.getY() );
		matrix.m23 = 0.0f;

		// Fourth row
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		matrix.m33 = 1.0f;

		return matrix;
	}

	public static Quaternion convertMatrix4fToQuaternion(Matrix4f matrix){
		Quaternion quat = new Quaternion();
		quat.setFromMatrix(matrix);
		return quat;

	}


	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}


	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	static public float atan2 (float y, float x) {
		if (x == 0f) {
			if (y > 0f) return PI / 2;
			if (y == 0f) return 0f;
			return -PI / 2;
		}
		final float atan, z = y / x;
		if (Math.abs(z) < 1f) {
			atan = z / (1f + 0.28f * z * z);
			if (x < 0f) return atan + (y < 0f ? -PI : PI);
			return atan;
		}
		atan = PI / 2 - z / (z * z + 0.28f);
		return y < 0f ? atan - PI : atan;
	}


	static public short clamp (short value, short min, short max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	static public int clamp (int value, int min, int max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	static public long clamp (long value, long min, long max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	static public float clamp (float value, float min, float max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	static public double clamp (double value, double min, double max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	static public boolean isEqual (float a, float b, float tolerance) {
		return Math.abs(a - b) <= tolerance;
	}

	static public boolean isEqual (float a, float b) {
		return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
	}
	static public float len(float a, float b, float c){
		return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(c, 2));
	}

	static public boolean isZero (float value) {
		return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
	}

	/** Returns true if the value is zero.
	 * @param tolerance represent an upper bound below which the value is considered zero. */
	static public boolean isZero (float value, float tolerance) {
		return Math.abs(value) <= tolerance;
	}
	public static float dot (float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	static public float len(Vector3f v){
		return (float) Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2) + Math.pow(v.z, 2));
	}
	static public float mul(Vector3f a, Vector3f b){
		return ((a.x * b.x) + (a.y * b.y) + (a.z * b.z));
	}

	static public float toDegree(float angle){
		return angle * radiansToDegrees;
	}

	static public float toDegree(double angle){
		return (float) angle * radiansToDegrees;
	}

	static public float toRadians(float angle){
		return angle * degreesToRadians;
	}

}
