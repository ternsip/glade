package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static com.ternsip.glade.graphics.general.Mesh.MIN_INTERNAL_SIZE;

@Getter
@Setter
public class Model {

    private final Mesh[] meshes;
    private final Animation animation;
    private final Vector3fc baseOffset;
    private final Vector3fc baseRotation;
    private final Vector3fc baseScale;

    @Getter(lazy = true)
    private final float internalSize = calcBiggestInternalSize(meshes);

    @Getter(lazy = true)
    private final Vector3fc boundSize = calcBoundSize(meshes);

    @Getter(lazy = true)
    private final Vector3fc lowestPoint = calcLowestPoint(meshes);

    @Getter(lazy = true)
    private final Vector3fc highestPoint = calcHighestPoint(meshes);

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

    private float calcBiggestInternalSize(Mesh[] meshes) {
        float biggestInternalSize = MIN_INTERNAL_SIZE;
        for (Mesh mesh : meshes) {
            biggestInternalSize = Math.max(biggestInternalSize, mesh.getInternalSize());
        }
        return biggestInternalSize;
    }

    private Vector3fc calcLowestPoint(Mesh[] meshes) {
        Vector3f lowestPoint = new Vector3f(Float.MAX_VALUE / 4);
        for (Mesh mesh : meshes) {
            lowestPoint = lowestPoint.min(mesh.getLowestPoint());
        }
        return lowestPoint;
    }

    private Vector3fc calcHighestPoint(Mesh[] meshes) {
        Vector3f highestPoint = new Vector3f(-Float.MAX_VALUE / 4);
        for (Mesh mesh : meshes) {
            highestPoint = highestPoint.max(mesh.getHighestPoint());
        }
        return highestPoint;
    }

    private Vector3fc calcBoundSize(Mesh[] meshes) {
        Vector3f boundSize = new Vector3f(-Float.MAX_VALUE / 4);
        for (Mesh mesh : meshes) {
            boundSize = boundSize.max(mesh.getBoundSize());
        }
        return boundSize;
    }

}
