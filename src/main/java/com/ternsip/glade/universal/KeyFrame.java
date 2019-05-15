package com.ternsip.glade.universal;

import lombok.Getter;

import java.util.Map;

@Getter
class KeyFrame {

    private final float timeStamp;
    private final Map<String, BoneTransform> boneKeyFrames;

    KeyFrame(float timeStamp, Map<String, BoneTransform> boneKeyFrames) {
        this.timeStamp = timeStamp;
        this.boneKeyFrames = boneKeyFrames;
    }

}
