package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Map;

import static com.ternsip.glade.graphics.general.Mesh.MIN_INTERNAL_SIZE;

@Getter
@Setter
public class Model {

    private final Mesh[] meshes;
    private final Animation animation;

    @Getter(lazy = true)
    private final float internalSize = calcBiggestInternalSize(meshes);

    @Getter(lazy = true)
    private final Vector3fc lowestPoint = calcLowestPoint(meshes);

    public Model(Mesh[] meshes) {
        this.meshes = meshes;
        this.animation = new Animation();
    }

    public Model(Mesh mesh) {
        this(new Mesh[]{mesh});
    }

    public Model(Mesh[] meshes, Bone rootBone, Map<String, AnimationFrames> animations) {
        this.meshes = meshes;
        this.animation = new Animation(rootBone, animations);
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

}
