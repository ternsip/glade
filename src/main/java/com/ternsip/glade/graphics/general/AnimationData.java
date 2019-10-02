package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

@Getter
public class AnimationData {

    private final Map<String, FrameTrack> nameToFrameTrack;
    private final int biggestBoneIndex;
    private final BoneIndexData[] boneIndexDataTopologicallySorted;

    public AnimationData() {
        this.nameToFrameTrack = new HashMap<>();
        this.biggestBoneIndex = 0;
        this.boneIndexDataTopologicallySorted = new BoneIndexData[0];
    }

    public AnimationData(Bone rootBone, Map<String, FrameTrack> nameToFrameTrack) {
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

    Matrix4fc[] calcBoneTransforms(AnimationTrack animationTrack) {
        Map<String, Matrix4fc> currentPose = animationTrack.calculateCurrentAnimationPose();
        Matrix4fc[] boneMatrices = new Matrix4fc[getBiggestBoneIndex() + 1];
        AnimationData.BoneIndexData[] boneIndexData = getBoneIndexDataTopologicallySorted();
        Matrix4fc[] boneAnimationTransforms = new Matrix4fc[boneIndexData.length];
        Matrix4fc[] boneInverseTransforms = new Matrix4fc[boneIndexData.length];
        boneAnimationTransforms[0] = new Matrix4f();
        boneInverseTransforms[0] = new Matrix4f();
        for (int i = 1; i < boneIndexData.length; ++i) {
            Bone bone = boneIndexData[i].getBone();
            int parentBoneOrder = boneIndexData[i].getParentBoneOrder();
            Matrix4fc parentAnimationTransform = boneAnimationTransforms[parentBoneOrder];
            Matrix4fc parentInverseTransform = boneInverseTransforms[parentBoneOrder];
            Matrix4fc localAnimationTransform = currentPose.get(bone.getName());
            if (localAnimationTransform == null) {
                boneAnimationTransforms[i] = parentAnimationTransform;
                boneInverseTransforms[i] = parentInverseTransform;
            } else {
                boneAnimationTransforms[i] = new Matrix4f(parentAnimationTransform).mul(localAnimationTransform);
                boneInverseTransforms[i] = new Matrix4f(bone.getInverseLocalBindTransform()).mul(parentInverseTransform);
            }
            if (bone.getIndex() != -1) {
                boneMatrices[bone.getIndex()] = new Matrix4f(boneAnimationTransforms[i]).mul(boneInverseTransforms[i]);
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
    private static class BoneIndexData {

        private final Bone bone;
        private final int parentBoneOrder;

    }

}
