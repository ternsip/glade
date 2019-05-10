package com.ternsip.glade.model.loader.animation.animation;

import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.loader.animation.model.Joint;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;


public class Animator {

    private final AnimatedModel entity;

    private Animation currentAnimation;
    private float animationTime = 0;


    public Animator(AnimatedModel entity) {
        this.entity = entity;
    }


    public void doAnimation(Animation animation) {
        this.animationTime = 0;
        this.currentAnimation = animation;
    }


    public void update() {
        if (currentAnimation == null) {
            return;
        }
        increaseAnimationTime();
        Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
        applyPoseToJoints(currentPose, entity.getRootJoint(), new Matrix4f());
    }

    private void increaseAnimationTime() {
        // TODO bind to delta between render shots in seconds = 1 / fps
        animationTime += 0.016;
        // animationTime += DisplayManager.getFrameTime();
        if (animationTime > currentAnimation.getLength()) {
            this.animationTime %= currentAnimation.getLength();
        }
    }

    private Map<String, Matrix4f> calculateCurrentAnimationPose() {
        KeyFrame[] frames = getPreviousAndNextFrames();
        float progression = calculateProgression(frames[0], frames[1]);
        return interpolatePoses(frames[0], frames[1], progression);
    }

    private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
        Matrix4f currentLocalTransform = currentPose.get(joint.name);
        Matrix4f currentTransform = parentTransform.mul(currentLocalTransform, new Matrix4f());
        for (Joint childJoint : joint.children) {
            applyPoseToJoints(currentPose, childJoint, currentTransform);
        }
        currentTransform.mul(joint.getInverseBindTransform(), currentTransform);
        joint.setAnimationTransform(currentTransform);
    }

    private KeyFrame[] getPreviousAndNextFrames() {
        KeyFrame[] allFrames = currentAnimation.getKeyFrames();
        KeyFrame previousFrame = allFrames[0];
        KeyFrame nextFrame = allFrames[0];
        for (int i = 1; i < allFrames.length; i++) {
            nextFrame = allFrames[i];
            if (nextFrame.getTimeStamp() > animationTime) {
                break;
            }
            previousFrame = allFrames[i];
        }
        return new KeyFrame[]{previousFrame, nextFrame};
    }


    private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
        float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
        float currentTime = animationTime - previousFrame.getTimeStamp();
        return currentTime / totalTime;
    }


    private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
        Map<String, Matrix4f> currentPose = new HashMap<>();
        for (String jointName : previousFrame.getJointKeyFrames().keySet()) {
            JointTransform previousTransform = previousFrame.getJointKeyFrames().get(jointName);
            JointTransform nextTransform = nextFrame.getJointKeyFrames().get(jointName);
            JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
            currentPose.put(jointName, currentTransform.getLocalTransform());
        }
        return currentPose;
    }

}
