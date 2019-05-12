package com.ternsip.glade.universal;

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
    private Bone rootBone;
    private int boneCount;

    private Animation currentAnimation;
    private float animationTime = 0;


    public Animator(Bone rootBone, int boneCount) {
        this.rootBone = rootBone;
        this.boneCount = boneCount;
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
        applyPoseToBones(currentPose, rootBone, new Matrix4f());
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

    private void applyPoseToBones(Map<String, Matrix4f> currentPose, Bone bone, Matrix4f parentTransform) {
        if (!currentPose.containsKey(bone.getName())) {
            for (Bone childBone : bone.getChildren()) {
                applyPoseToBones(currentPose, childBone, parentTransform);
            }
            return;
        }
        Matrix4f currentLocalTransform = currentPose.get(bone.getName());
        Matrix4f currentTransform = parentTransform.mul(currentLocalTransform, new Matrix4f());
        for (Bone childBone : bone.getChildren()) {
            applyPoseToBones(currentPose, childBone, currentTransform);
        }
        currentTransform.mul(bone.getInverseBindTransform(), currentTransform);
        bone.setAnimatedTransform(currentTransform);
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
        for (String boneName : previousFrame.getBoneKeyFrames().keySet()) {
            BoneTransform previousTransform = previousFrame.getBoneKeyFrames().get(boneName);
            BoneTransform nextTransform = nextFrame.getBoneKeyFrames().get(boneName);
            BoneTransform currentTransform = BoneTransform.interpolate(previousTransform, nextTransform, progression);
            currentPose.put(boneName, currentTransform.getLocalTransform());
        }
        return currentPose;
    }

    public Matrix4f[] getBoneTransforms() {
        Matrix4f[] boneMatrices = new Matrix4f[boneCount];
        addBonesToArray(rootBone, boneMatrices);

        // TODO this is just dummy to prevent crashing
        for (int i = 0; i < boneMatrices.length; ++i) {
            if (boneMatrices[i] == null) {
                boneMatrices[i] = new Matrix4f();
            }
        }

        return boneMatrices;
    }

    private void addBonesToArray(Bone bone, Matrix4f[] boneMatrices) {
        // TODO this if is just dummy to prevent crashing
        if (bone.getIndex() >= 0 && bone.getIndex() < boneMatrices.length) {
            boneMatrices[bone.getIndex()] = bone.getAnimatedTransform();
        }
        for (Bone childBone : bone.getChildren()) {
            addBonesToArray(childBone, boneMatrices);
        }
    }

}
