package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@NonNull
class FrameTrack {

    private final float lengthSeconds;
    private final KeyFrame[] keyFrames;

    FrameTrack() {
        this.lengthSeconds = 0;
        this.keyFrames = new KeyFrame[]{};
    }

    Set<String> findAllDistinctBonesNames() {
        Set<String> boneNames = new HashSet<>();
        for (KeyFrame keyFrame : keyFrames) {
            boneNames.addAll(keyFrame.getBoneKeyFrames().keySet());
        }
        return boneNames;
    }

}
