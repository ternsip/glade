package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

@Getter
@Setter
class Animation {

    private final Map<String, AnimationFrames> nameToAnimation;
    private final int biggestBoneIndex;
    private final BoneIndexData[] boneIndexDataTopologicallySorted;

    Animation() {
        this(new Bone(), Collections.emptyMap());
    }

    Animation(Bone rootBone, Map<String, AnimationFrames> nameToAnimation) {
        this.nameToAnimation = nameToAnimation;
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

    @RequiredArgsConstructor
    @Getter
    class BoneIndexData {

        private final Bone bone;
        private final int parentBoneOrder;

    }

}
