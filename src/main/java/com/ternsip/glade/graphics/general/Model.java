package com.ternsip.glade.graphics.general;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedAssignment")
@Builder
@Getter
public class Model {

    @Builder.Default
    private List<Mesh> meshes = new ArrayList<>();

    @Builder.Default
    private Vector3fc baseOffset = new Vector3f(0);

    @Builder.Default
    private Vector3fc baseRotation = new Vector3f(0);

    @Builder.Default
    private Vector3fc baseScale = new Vector3f(1);

    @Builder.Default
    private AnimationData animationData = new AnimationData();

    @Getter(lazy = true)
    private final float normalizingScale = calcNormalizedScale(meshes);

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
