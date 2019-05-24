package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

@Getter
public class Model {

    private final Mesh[] meshes;
    private final Map<String, FrameTrack> nameToFrameTrack;
    private final int biggestBoneIndex;
    private final BoneIndexData[] boneIndexDataTopologicallySorted;
    private final Vector3fc baseOffset;
    private final Vector3fc baseRotation;
    private final Vector3fc baseScale;

    @Getter(lazy = true)
    private final float normalizingScale = calcNormalizedScale(meshes);

    public Model() {
        this(new Mesh[]{});
    }

    public Model(Mesh mesh) {
        this(new Mesh[]{mesh});
    }

    public Model(Mesh[] meshes) {
        this(meshes, new Bone(), Collections.emptyMap());
    }

    public Model(Mesh[] meshes, Bone rootBone, Map<String, FrameTrack> nameToFrameTrack) {
        this(meshes, new Vector3f(0), new Vector3f(0), new Vector3f(1), rootBone, nameToFrameTrack);
    }

    public Model(
            Mesh[] meshes,
            Vector3fc baseOffset,
            Vector3fc baseRotation,
            Vector3fc baseScale
    ) {
        this(meshes, baseOffset, baseRotation, baseScale, new Bone(), Collections.emptyMap());
    }

    public Model(
            Mesh[] meshes,
            Vector3fc baseOffset,
            Vector3fc baseRotation,
            Vector3fc baseScale,
            Bone rootBone,
            Map<String, FrameTrack> nameToFrameTrack
    ) {
        this.meshes = meshes;
        this.baseOffset = baseOffset;
        this.baseRotation = baseRotation;
        this.baseScale = baseScale;

        this.nameToFrameTrack = nameToFrameTrack;
        Stack<BoneIndexData> bonesStack = new Stack<>();
        bonesStack.push(new BoneIndexData(rootBone, -1));
        int biggestBoneIndex = 0;
        ArrayList<BoneIndexData> topSortBones = new ArrayList<>();
        for (int i = 0; !bonesStack.isEmpty(); ++i) {
            BoneIndexData topBoneIndexData = bonesStack.pop();
            topSortBones.add(topBoneIndexData);
            Bone bone = topBoneIndexData.getBone();
            final int currentBoneOrder = i;
            bonesStack.addAll(bone.getChildren().stream().map(e -> new BoneIndexData(e, currentBoneOrder)).collect(Collectors.toList()));
            biggestBoneIndex = Math.max(biggestBoneIndex, bone.getIndex());
        }
        this.biggestBoneIndex = biggestBoneIndex;
        this.boneIndexDataTopologicallySorted = topSortBones.toArray(new BoneIndexData[0]);
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

    Matrix4f[] calcBoneTransforms(AnimationTrack animationTrack) {
        Map<String, Matrix4f> currentPose = animationTrack.calculateCurrentAnimationPose();
        Matrix4f[] boneMatrices = new Matrix4f[getBiggestBoneIndex() + 1];
        Model.BoneIndexData[] boneIndexData = getBoneIndexDataTopologicallySorted();
        Matrix4f[] parentTransforms = new Matrix4f[boneIndexData.length];
        for (int i = 0; i < boneIndexData.length; ++i) {
            Bone bone = boneIndexData[i].getBone();
            int parentBoneOrder = boneIndexData[i].getParentBoneOrder();
            Matrix4f parentTransform = parentBoneOrder < 0 ? new Matrix4f() : parentTransforms[parentBoneOrder];
            Matrix4f currentLocalTransform = currentPose.getOrDefault(bone.getName(), new Matrix4f());
            Matrix4f currentTransform = parentTransform.mul(currentLocalTransform, new Matrix4f());
            parentTransforms[i] = new Matrix4f(currentTransform);
            if (bone.getIndex() >= 0) {
                currentTransform.mul(bone.getInverseBindTransform(), currentTransform);
                boneMatrices[bone.getIndex()] = currentTransform;
            }
        }
        return boneMatrices;
    }

    AnimationTrack getAnimationTrack(String name) {
        if (getNameToFrameTrack().containsKey(name)) {
            return new AnimationTrack(getNameToFrameTrack().get(name));
        }
        return new AnimationTrack(getNameToFrameTrack().values().stream().findFirst().orElse(new FrameTrack()));
    }

    @RequiredArgsConstructor
    @Getter
    private class BoneIndexData {

        private final Bone bone;
        private final int parentBoneOrder;

    }

}
