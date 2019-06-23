package com.ternsip.glade.common.logic;

import org.joml.*;

import java.lang.Math;

public class Maths {

    public static final float EPS = 1e-5f;

    public static final Vector3fc UP_DIRECTION = new Vector3f(0, 1, 0);
    public static final Vector3fc DOWN_DIRECTION = new Vector3f(0, -1, 0);
    public static final Vector3fc BACK_DIRECTION = new Vector3f(0, 0, -1);
    public static final Vector3fc FRONT_DIRECTION = new Vector3f(0, 0, 1);
    public static final Vector3fc LEFT_DIRECTION = new Vector3f(1, 0, 0);
    public static final Vector3fc RIGHT_DIRECTION = new Vector3f(-1, 0, 0);

    public static Matrix4f createTransformationMatrix(Vector3fc translation, Quaternionfc rotation, Vector3fc scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation, matrix);
        matrix.mul(rotation.get(new Matrix4f()), matrix);
        matrix.scale(scale, matrix);
        return matrix;
    }

    public static Quaternionfc getRotationQuaternion(Vector3f rotation) {
        float roll = rotation.x();
        float pitch = rotation.y();
        float yaw = rotation.z();

        float cy = (float) Math.cos(yaw * 0.5f);
        float sy = (float) Math.sin(yaw * 0.5f);
        float cp = (float) Math.cos(pitch * 0.5f);
        float sp = (float) Math.sin(pitch * 0.5f);
        float cr = (float) Math.cos(roll * 0.5f);
        float sr = (float) Math.sin(roll * 0.5f);

        return new Quaternionf(
                cy * cp * sr - sy * sp * cr,
                sy * cp * sr + cy * sp * cr,
                sy * cp * cr - cy * sp * sr,
                cy * cp * cr + sy * sp * sr
        );
    }

    public static Vector3fc normalizeOrEmpty(Vector3fc v) {
        return v.lengthSquared() <= EPS ? v : v.normalize(new Vector3f());
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rot, float scale) {
        Matrix4f matrix = new Matrix4f();

        Quaternionf roll = new Quaternionf();
        roll.fromAxisAngleDeg(0f, 0f, -1f, rot.z * 90F);
        roll.normalize();

        Quaternionf pitch = new Quaternionf();
        pitch.fromAxisAngleDeg(-1f, 0f, 0f, rot.x * 90F);
        pitch.normalize();

        Quaternionf yaw = new Quaternionf();
        yaw.fromAxisAngleDeg(0f, 1f, 0f, rot.y * 90F);
        yaw.normalize();

        Quaternionf rotation = roll.mul(pitch, new Quaternionf()).mul(yaw, new Quaternionf());

        matrix.identity();
        matrix.translate(translation, matrix);
        matrix.mul(rotation.get(new Matrix4f()), matrix);
        matrix.scale(scale, scale, scale, matrix);
        return matrix;
    }

    /**
     * Interpolates between two quaternion rotations and returns the resulting
     * quaternion rotation. The interpolation method here is "nlerp", or
     * "normalized-lerp". Another mnethod that could be used is "slerp", and you
     * can see a comparison of the methods here:
     * https://keithmaggio.wordpress.com/2011/02/15/math-magician-lerp-slerp-and-nlerp/
     * <p>
     * and here:
     * http://number-none.com/product/Understanding%20Slerp,%20Then%20Not%20Using%20It/
     *
     * @param a
     * @param b
     * @param blend - a value between 0 and 1 indicating how far to interpolate
     *              between the two quaternions.
     * @return The resulting interpolated rotation in quaternion format.
     */
    public static Quaternionfc interpolate(Quaternionfc a, Quaternionfc b, float blend) {
        float dot = a.w() * b.w() + a.x() * b.x() + a.y() * b.y() + a.z() * b.z();
        float blendI = 1f - blend;
        if (dot < 0) {
            return new Quaternionf(
                    blendI * a.x() + blend * -b.x(),
                    blendI * a.y() + blend * -b.y(),
                    blendI * a.z() + blend * -b.z(),
                    blendI * a.w() + blend * -b.w()
            ).normalize();
        }
        return new Quaternionf(
                blendI * a.x() + blend * b.x(),
                blendI * a.y() + blend * b.y(),
                blendI * a.z() + blend * b.z(),
                blendI * a.w() + blend * b.w()
        ).normalize();
    }

    public static Vector4fc mul(Matrix4fc mat, Vector4fc vec) {
        return new Vector4f(
                vec.x() * mat.m00() + vec.y() * mat.m10() + vec.z() * mat.m20() + vec.w() * mat.m30(),
                vec.x() * mat.m01() + vec.y() * mat.m11() + vec.z() * mat.m21() + vec.w() * mat.m31(),
                vec.x() * mat.m02() + vec.y() * mat.m12() + vec.z() * mat.m22() + vec.w() * mat.m32(),
                vec.x() * mat.m03() + vec.y() * mat.m13() + vec.z() * mat.m23() + vec.w() * mat.m33()
        );
    }

    public static float log2(float d) {
        return (float) (Math.log(d)/Math.log(2.0));
    }

}
