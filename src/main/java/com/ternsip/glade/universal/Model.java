package com.ternsip.glade.universal;

import com.ternsip.glade.utils.Maths;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.Map;

@Getter
@Setter
public class Model {

    private final Mesh[] meshes;
    private final Vector3f position;
    private final Vector3f scale;
    private final Vector3f rotation;
    private final Animator animator;

    public Model(Mesh[] meshes) {
        this.meshes = meshes;
        this.position = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.rotation = new Vector3f(0, 0, 0);
        this.animator = new Animator();
    }

    public Model(Mesh mesh) {
        this(new Mesh[]{mesh});
    }

    public Model(Mesh[] meshes, Bone rootBone, int boneCount, Map<String, Animation> animations) {
        this.meshes = meshes;
        this.position = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.rotation = new Vector3f(0, 0, 0);
        this.animator = new Animator(rootBone, boneCount, animations);
    }

    public void cleanup() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }

    public Quaternionfc getRotationQuaternion() {
        float attitude = Maths.toRadians(rotation.x());
        float heading = Maths.toRadians(rotation.y());
        float bank = Maths.toRadians(rotation.z());

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

    public Matrix4f getTransformationMatrix() {
        return Maths.createTransformationMatrix(getPosition(), getRotationQuaternion(), getScale());
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

}
