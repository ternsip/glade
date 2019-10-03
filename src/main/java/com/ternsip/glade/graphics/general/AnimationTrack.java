package com.ternsip.glade.graphics.general;

import lombok.Getter;
import org.joml.Matrix4fc;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AnimationTrack {

    private final String name;
    private final FrameTrack frameTrack;
    private final long startTime;

    AnimationTrack(String name, FrameTrack frameTrack) {
        this.name = name;
        this.frameTrack = frameTrack;
        this.startTime = System.currentTimeMillis();
    }

    Map<String, Matrix4fc> calculateCurrentAnimationPose() {
        float animationTimeDeltaSeconds = (System.currentTimeMillis() - getStartTime()) / 1000f;
        float animationTime = animationTimeDeltaSeconds % getFrameTrack().getLengthSeconds();
        KeyFrame[] allFrames = getFrameTrack().getKeyFrames();
        float duration = Math.max(0.1f, getFrameTrack().getLengthSeconds());
        int frameNumber = allFrames.length;
        float deltaTime = frameNumber == 1 ? duration : (duration / (frameNumber - 1));
        int frameIndex = Math.max(0, (int) (animationTime / deltaTime));
        KeyFrame currentFrame = allFrames[frameIndex];
        KeyFrame nextFrame = allFrames[(frameIndex + 1) % frameNumber];
        float progression = (animationTime % deltaTime) / deltaTime;
        Map<String, Matrix4fc> currentPose = new HashMap<>();
        for (String boneName : currentFrame.getBoneKeyFrames().keySet()) {
            BoneTransform previousTransform = currentFrame.getBoneKeyFrames().get(boneName);
            BoneTransform nextTransform = nextFrame.getBoneKeyFrames().get(boneName);
            BoneTransform currentTransform = BoneTransform.interpolate(previousTransform, nextTransform, progression);
            currentPose.put(boneName, currentTransform.getLocalTransform());
        }
        return currentPose;
    }

    boolean isEmpty() {
        return getFrameTrack().getKeyFrames().length == 0;
    }

}
