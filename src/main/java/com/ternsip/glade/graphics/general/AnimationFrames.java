package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@NonNull
public class AnimationFrames {

    private final float lengthSeconds;
    private final KeyFrame[] keyFrames;

    Set<String> findAllDistinctBonesNames() {
        Set<String> boneNames = new HashSet<>();
        for (KeyFrame keyFrame : keyFrames) {
            boneNames.addAll(keyFrame.getBoneKeyFrames().keySet());
        }
        return boneNames;
    }

}
