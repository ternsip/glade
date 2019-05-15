package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;
import java.util.Stack;

@Getter
@Setter
class Animation {

    private final Bone rootBone;
    private final Map<String, AnimationFrames> nameToAnimation;
    private final int biggestBoneIndex;

    Animation() {
        this(new Bone(), Collections.emptyMap());
    }

    Animation(Bone rootBone, Map<String, AnimationFrames> nameToAnimation) {
        this.rootBone = rootBone;
        this.biggestBoneIndex = calcBiggestBoneIndex(rootBone);
        this.nameToAnimation = nameToAnimation;
    }

    private static int calcBiggestBoneIndex(Bone rootBone) {
        Stack<Bone> bonesStack = new Stack<>();
        bonesStack.push(rootBone);
        int biggestBoneIndex = 0;
        while (!bonesStack.isEmpty()) {
            Bone topBone = bonesStack.pop();
            bonesStack.addAll(topBone.getChildren());
            biggestBoneIndex = Math.max(biggestBoneIndex, topBone.getIndex());
        }
        return biggestBoneIndex;
    }

}
