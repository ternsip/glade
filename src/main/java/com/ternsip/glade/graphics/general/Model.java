package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Getter
@Setter
public class Model {

    private final Mesh[] meshes;
    private final Animation animation;
    private final Vector3fc baseOffset;
    private final Vector3fc baseRotation;
    private final Vector3fc baseScale;

    @Getter(lazy = true)
    private final float normalizingScale = calcNormalizedScale(meshes);

    public Model(Mesh[] meshes) {
        this(meshes, new Animation());
    }

    public Model(Mesh mesh) {
        this(new Mesh[]{mesh});
    }

    public Model(Mesh[] meshes, Animation animation) {
        this(meshes, animation, new Vector3f(0), new Vector3f(0), new Vector3f(1));
    }

    public Model(
            Mesh[] meshes,
            Animation animation,
            Vector3fc baseOffset,
            Vector3fc baseRotation,
            Vector3fc baseScale
    ) {
        this.meshes = meshes;
        this.animation = animation;
        this.baseOffset = baseOffset;
        this.baseRotation = baseRotation;
        this.baseScale = baseScale;
    }

    public void finish() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].finish();
        }
    }

    private float calcNormalizedScale(Mesh[] meshes) {
        float smallestScale = Float.MAX_VALUE / 4;
        for (Mesh mesh : meshes) {
            smallestScale = Math.min(smallestScale, mesh.getNormalizingScale());
        }
        return smallestScale;
    }

}
