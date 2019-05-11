package com.ternsip.glade.model.loader.animation.animation;

import lombok.Getter;

import java.util.Map;

// TODO make it possible to have non-same number of keyframes per each joint (less info), Add methods getting for jointTransforms
@Getter
public class KeyFrame {

    private final float timeStamp;
    private final Map<String, JointTransform> jointKeyFrames;

    public KeyFrame(float timeStamp, Map<String, JointTransform> jointKeyFrames) {
        this.timeStamp = timeStamp;
        this.jointKeyFrames = jointKeyFrames;
    }

}
