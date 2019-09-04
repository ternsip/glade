package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;

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

    Matrix4f[] calcBoneTransforms(AnimationTrack animationTrack) {
        Map<String, Matrix4f> currentPose = animationTrack.calculateCurrentAnimationPose();
        Matrix4f[] boneMatrices = new Matrix4f[getBiggestBoneIndex() + 1];
        AnimationData.BoneIndexData[] boneIndexData = getBoneIndexDataTopologicallySorted();
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
    private static class BoneIndexData {

        private final Bone bone;
        private final int parentBoneOrder;

    }

}
