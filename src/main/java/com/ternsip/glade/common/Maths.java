package com.ternsip.glade.common;

import org.joml.*;

import java.lang.Math;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3fc translation, Quaternionfc rotation, Vector3fc scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation, matrix);
        matrix.mul(rotation.get(new Matrix4f()), matrix);
        matrix.scale(scale, matrix);
        return matrix;
    }

    public static Quaternionfc getRotationQuaternion(Vector3f rotation) {
        float attitude = rotation.x();
        float heading = rotation.y();
        float bank = rotation.z();

        // Assuming the angles are in radians.
        float c1 = (float) Math.cos(heading);
        float s1 = (float) Math.sin(heading);
        float c2 = (float) Math.cos(attitude);
        float s2 = (float) Math.sin(attitude);
        float c3 = (float) Math.cos(bank);
        float s3 = (float) Math.sin(bank);
        float w = (float) (Math.sqrt(1.0 + c1 * c2 + c1 * c3 - s1 * s2 * s3 + c2 * c3) / 2.0);
        float w4 = (4.0f * w);
        float x = (c2 * s3 + c1 * s3 + s1 * s2 * c3) / w4;
        float y = (s1 * c2 + s1 * c3 + c1 * s2 * s3) / w4;
        float z = (-s1 * s3 + c1 * s2 * c3 + s2) / w4;
        return new Quaternionf(x, y, z, w);
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

}
