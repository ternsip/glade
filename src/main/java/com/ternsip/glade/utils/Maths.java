package com.ternsip.glade.utils;

import org.joml.*;

import java.lang.Math;

public class Maths {

    public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static final float PI = 3.1415927f;
    public static final float PI2 = PI * 2;
    public static final float radiansToDegrees = 180f / PI;
    public static final float degreesToRadians = PI / 180;
    public static final float PI_OVER_180 = PI / 180.0f;
    public static final float PI_UNDER_180 = 180.0f / PI;


    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation, matrix);
        matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix);
        matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix);
        matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix);
        matrix.scale(new Vector3f(scale, scale, scale), matrix);
        return matrix;
    }


    public static Matrix4f createTransformationMatrix(Vector3f translation, Quaternionfc rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();

        matrix.identity();
        matrix.translate(translation, matrix);
        matrix.mul(convertQuaternionToMatrix4f(rotation), matrix);
        matrix.scale(scale, matrix);
        return matrix;
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
        matrix.mul(convertQuaternionToMatrix4f(rotation), matrix);
        matrix.scale(scale, scale, scale, matrix);
        return matrix;
    }

    private static Matrix4f convertQuaternionToMatrix4f(Quaternionfc q) {
        return new Matrix4f(
                // First row
                1.0f - 2.0f * (q.y() * q.y() + q.z() * q.z()),
                2.0f * (q.x() * q.y() + q.z() * q.w()),
                2.0f * (q.x() * q.z() - q.y() * q.w()),
                0.0f,

                // Second row
                2.0f * (q.x() * q.y() - q.z() * q.w()),
                1.0f - 2.0f * (q.x() * q.x() + q.z() * q.z()),
                2.0f * (q.z() * q.y() + q.x() * q.w()),
                0.0f,

                // Third row
                2.0f * (q.x() * q.z() + q.y() * q.w()),
                2.0f * (q.y() * q.z() - q.x() * q.w()),
                1.0f - 2.0f * (q.x() * q.x() + q.y() * q.y()),
                0.0f,

                // Fourth row
                0,
                0,
                0,
                1.0f
        );
    }

    //TODO ALREADY EXISTS!11, this might be better

    /**
     * Converts the quaternion to a 4x4 matrix representing the exact same
     * rotation as this quaternion. (The rotation is only contained in the
     * top-left 3x3 part, but a 4x4 matrix is returned here for convenience
     * seeing as it will be multiplied with other 4x4 matrices).
     * <p>
     * More detailed explanation here:
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
     *
     * @return The rotation matrix which represents the exact same rotation as
     * this quaternion.
     */
    public static Matrix4f toRotationMatrix(Quaternionfc quaternionfc) {
        final float x = quaternionfc.x();
        final float y = quaternionfc.y();
        final float z = quaternionfc.z();
        final float w = quaternionfc.w();
        final float xy = x * y;
        final float xz = x * z;
        final float xw = x * w;
        final float yz = y * z;
        final float yw = y * w;
        final float zw = z * w;
        final float xSquared = x * x;
        final float ySquared = y * y;
        final float zSquared = z * z;
        return new Matrix4f(
                1 - 2 * (ySquared + zSquared),
                2 * (xy - zw),
                2 * (xz + yw),
                0,
                2 * (xy + zw),
                1 - 2 * (xSquared + zSquared),
                2 * (yz - xw),
                0,
                2 * (xz - yw),
                2 * (yz + xw),
                1 - 2 * (xSquared + ySquared),
                0,
                0,
                0,
                0,
                1
        );
    }


    // TODO PROBABLY ALREADY EXISTS (setFromUnnormalized)

    /**
     * Extracts the rotation part of a transformation matrix and converts it to
     * a quaternion using the magic of maths.
     * <p>
     * More detailed explanation here:
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/index.htm
     *
     * @param matrix - the transformation matrix containing the rotation which this
     *               quaternion shall represent.
     */
    public static Quaternionfc fromMatrix(Matrix4f matrix) {
        float w, x, y, z;
        float diagonal = matrix.m00() + matrix.m11() + matrix.m22();
        if (diagonal > 0) {
            float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
            w = w4 / 4f;
            x = (matrix.m21() - matrix.m12()) / w4;
            y = (matrix.m02() - matrix.m20()) / w4;
            z = (matrix.m10() - matrix.m01()) / w4;
        } else if ((matrix.m00() > matrix.m11()) && (matrix.m00() > matrix.m22())) {
            float x4 = (float) (Math.sqrt(1f + matrix.m00() - matrix.m11() - matrix.m22()) * 2f);
            w = (matrix.m21() - matrix.m12()) / x4;
            x = x4 / 4f;
            y = (matrix.m01() + matrix.m10()) / x4;
            z = (matrix.m02() + matrix.m20()) / x4;
        } else if (matrix.m11() > matrix.m22()) {
            float y4 = (float) (Math.sqrt(1f + matrix.m11() - matrix.m00() - matrix.m22()) * 2f);
            w = (matrix.m02() - matrix.m20()) / y4;
            x = (matrix.m01() + matrix.m10()) / y4;
            y = y4 / 4f;
            z = (matrix.m12() + matrix.m21()) / y4;
        } else {
            float z4 = (float) (Math.sqrt(1f + matrix.m22() - matrix.m00() - matrix.m11()) * 2f);
            w = (matrix.m10() - matrix.m01()) / z4;
            x = (matrix.m02() + matrix.m20()) / z4;
            y = (matrix.m12() + matrix.m21()) / z4;
            z = z4 / 4f;
        }
        return new Quaternionf(x, y, z, w).normalize();
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
        Quaternionfc result = new Quaternionf(0, 0, 0, 1).normalize();
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

    public static Quaternionf convertMatrix4fToQuaternion(Matrix4f matrix) {
        Quaternionf quat = new Quaternionf();
        quat.setFromUnnormalized(matrix);
        return quat;

    }

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(new Vector3f(translation.x, translation.y, 0f), matrix);
        matrix.scale(new Vector3f(scale.x, scale.y, 1f), matrix);
        return matrix;
    }

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    static public float atan2(float y, float x) {
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


    static public short clamp(short value, short min, short max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public long clamp(long value, long min, long max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    static public boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    static public float len(float a, float b, float c) {
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(c, 2));
    }

    static public boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if the value is zero.
     *
     * @param tolerance represent an upper bound below which the value is considered zero.
     */
    static public boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    static public float len(Vector3f v) {
        return (float) Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2) + Math.pow(v.z, 2));
    }

    static public float mul(Vector3f a, Vector3f b) {
        return ((a.x * b.x) + (a.y * b.y) + (a.z * b.z));
    }

    static public float toDegree(float angle) {
        return angle * radiansToDegrees;
    }

    static public float toDegree(double angle) {
        return (float) angle * radiansToDegrees;
    }

    static public float toRadians(float angle) {
        return angle * degreesToRadians;
    }

}
