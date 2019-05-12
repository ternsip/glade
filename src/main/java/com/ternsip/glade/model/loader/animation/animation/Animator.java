package com.ternsip.glade.model.loader.animation.animation;

import com.ternsip.glade.model.loader.animation.model.Joint;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@Getter
@Setter
public class Animator {


    // skeleton
    private Joint rootJoint;
    private int jointCount;

    private Animation currentAnimation;
    private float animationTime = 0;


    public Animator(Joint rootJoint, int jointCount) {
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
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
        applyPoseToJoints(currentPose, rootJoint, new Matrix4f());
    }

    private void increaseAnimationTime() {
        animationTime += DISPLAY_MANAGER.getDeltaTime();
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
        if (!currentPose.containsKey(joint.getName())) {
            for (Joint childJoint : joint.children) {
                applyPoseToJoints(currentPose, childJoint, parentTransform);
            }
            return;
        }
        Matrix4f currentLocalTransform = currentPose.get(joint.name);
        Matrix4f currentTransform = parentTransform.mul(currentLocalTransform, new Matrix4f());
        for (Joint childJoint : joint.children) {
            applyPoseToJoints(currentPose, childJoint, currentTransform);
        }
        currentTransform.mul(joint.getInverseBindTransform(), currentTransform);
        joint.setAnimatedTransform(currentTransform);
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

    public Matrix4f[] getJointTransforms() {
        Matrix4f[] jointMatrices = new Matrix4f[jointCount];
        addJointsToArray(rootJoint, jointMatrices);

        // TODO this is just dummy to prevent crashing
        for (int i = 0; i < jointMatrices.length; ++i) {
            if (jointMatrices[i] == null) {
                jointMatrices[i] = new Matrix4f();
            }
        }

        return jointMatrices;
    }

    private void addJointsToArray(Joint joint, Matrix4f[] jointMatrices) {
        // TODO this if is just dummy to prevent crashing
        if (joint.index >= 0 && joint.index < jointMatrices.length) {
            jointMatrices[joint.index] = joint.getAnimatedTransform();
        }
        for (Joint childJoint : joint.children) {
            addJointsToArray(childJoint, jointMatrices);
        }
    }

}
