package com.ternsip.glade.universal;

import lombok.Getter;

import java.util.Map;

// TODO make it possible to have non-same number of keyframes per each joint (less info), Add methods getting for boneTransforms
@Getter
public class KeyFrame {

    private final float timeStamp;
    private final Map<String, BoneTransform> boneKeyFrames;

    public KeyFrame(float timeStamp, Map<String, BoneTransform> boneKeyFrames) {
        this.timeStamp = timeStamp;
        this.boneKeyFrames = boneKeyFrames;
    }

}
