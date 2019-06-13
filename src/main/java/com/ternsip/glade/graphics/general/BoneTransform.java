package com.ternsip.glade.graphics.general;

import com.ternsip.glade.common.logic.Maths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@RequiredArgsConstructor
@Getter
class BoneTransform {

    private final Vector3fc position;
    private final Vector3fc scaling;
    private final Quaternionfc rotation;

    static BoneTransform interpolate(BoneTransform frameA, BoneTransform frameB, float progression) {
        Vector3f pos = interpolate(frameA.getPosition(), frameB.getPosition(), progression);
        Vector3f scale = interpolate(frameA.getScaling(), frameB.getScaling(), progression);
        Quaternionfc rot = Maths.interpolate(frameA.getRotation(), frameB.getRotation(), progression);
        return new BoneTransform(pos, scale, rot);
    }


    private static Vector3f interpolate(Vector3fc start, Vector3fc end, float progression) {
        float x = start.x() + (end.x() - start.x()) * progression;
        float y = start.y() + (end.y() - start.y()) * progression;
        float z = start.z() + (end.z() - start.z()) * progression;
        return new Vector3f(x, y, z);
    }

    public Matrix4f getLocalTransform() {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(position);
        matrix.mul(rotation.get(new Matrix4f()), matrix);
        matrix.scale(scaling, matrix);
        return matrix;
    }

}
