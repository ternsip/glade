package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class Model {

    private final List<Mesh> meshes;
    private final Vector3fc baseOffset;
    private final Vector3fc baseRotation;
    private final Vector3fc baseScale;
    private final Map<String, FrameTrack> nameToFrameTrack;
    private final float normalizingScale;

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
        this(meshes, baseOffset, baseRotation, baseScale,  new HashMap<>());
    }

    public Model(List<Mesh> meshes, Vector3fc baseOffset, Vector3fc baseRotation, Vector3fc baseScale, Map<String, FrameTrack> nameToFrameTrack) {
        this(meshes, baseOffset, baseRotation, baseScale,  nameToFrameTrack, calcNormalizedScale(meshes));
    }

    public void finish() {
        for (Mesh mesh : getMeshes()) {
            mesh.finish();
        }
    }

    public AnimationTrack getAnimationTrack(String name) {
        if (getNameToFrameTrack().containsKey(name)) {
            return new AnimationTrack(getNameToFrameTrack().get(name));
        }
        return new AnimationTrack(getNameToFrameTrack().values().stream().findFirst().orElse(new FrameTrack()));
    }

    public boolean isAnimated() {
        return getNameToFrameTrack().size() > 0;
    }

    private static float calcNormalizedScale(List<Mesh> meshes) {
        float smallestScale = Float.MAX_VALUE / 4;
        for (Mesh mesh : meshes) {
            smallestScale = Math.min(smallestScale, mesh.getNormalizingScale());
        }
        return smallestScale;
    }

}
