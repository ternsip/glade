package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
class AnimationFrames {

    private final float length; // In seconds
    private final KeyFrame[] keyFrames;

    Set<String> findAllDistinctBonesNames() {
        Set<String> boneNames = new HashSet<>();
        for (int i = 0; i < keyFrames.length; ++i) {
            boneNames.addAll(keyFrames[i].getBoneKeyFrames().keySet());
        }
        return boneNames;
    }

}
