package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
class AnimationFrames {

    // TODO ADD RESTRICTION = MIN VALUE 0.1 CAN DEVISION BY ZERO!
    private final float lengthSeconds;
    private final KeyFrame[] keyFrames;

    Set<String> findAllDistinctBonesNames() {
        Set<String> boneNames = new HashSet<>();
        for (int i = 0; i < keyFrames.length; ++i) {
            boneNames.addAll(keyFrames[i].getBoneKeyFrames().keySet());
        }
        return boneNames;
    }

}
