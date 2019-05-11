package com.ternsip.glade.universal;

import com.ternsip.glade.utils.Maths;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Getter
@Setter
public class GameItem {

    private boolean selected;

    private Mesh[] meshes;

    private Vector3f position;

    private Vector3f scale;

    private Vector3f rotation;

    private int textPos;
    
    private boolean disableFrustumCulling;

    private boolean insideFrustum;

    public GameItem() {
        selected = false;
        position = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
        rotation = new Vector3f(0, 0, 0);
        textPos = 0;
        insideFrustum = true;
        disableFrustumCulling = false;
    }

    public GameItem(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
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

}
