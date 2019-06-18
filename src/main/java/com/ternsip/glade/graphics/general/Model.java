package com.ternsip.glade.graphics.general;

import lombok.Getter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Model {

    private final List<Mesh> meshes;
    private final Vector3fc baseOffset;
    private final Vector3fc baseRotation;
    private final Vector3fc baseScale;
    private final float normalizingScale;
    private final AnimationData animationData;

    public Model() {
        this(new ArrayList<>());
    }

    public Model(Mesh mesh) {
        this(Collections.singletonList(mesh));
    }

    public Model(List<Mesh> meshes) {
        this(meshes, new Vector3f(0), new Vector3f(0), new Vector3f(1));
    }

    public Model(List<Mesh> meshes, Vector3fc baseOffset, Vector3fc baseRotation, Vector3fc baseScale) {
        this(meshes, baseOffset, baseRotation, baseScale, new AnimationData());
    }

    public Model(List<Mesh> meshes, Vector3fc baseOffset, Vector3fc baseRotation, Vector3fc baseScale, AnimationData animationData) {
        this.meshes = meshes;
        this.baseOffset = baseOffset;
        this.baseRotation = baseRotation;
        this.baseScale = baseScale;
        this.normalizingScale = calcNormalizedScale(meshes);
        this.animationData = animationData;
    }

    public void finish() {
        for (Mesh mesh : getMeshes()) {
            mesh.finish();
        }
    }

    private float calcNormalizedScale(List<Mesh> meshes) {
        float smallestScale = Float.MAX_VALUE / 4;
        for (Mesh mesh : meshes) {
            smallestScale = Math.min(smallestScale, mesh.getNormalizingScale());
        }
        return smallestScale;
    }

}
