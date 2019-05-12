package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@Getter
@Setter
public class Animator {

    private final Bone rootBone;
    private final int boneCount;
    private final Map<String, Animation> nameToAnimation;

    private Animation currentAnimation;
    private float animationTime = 0;

    public Animator() {
        this.rootBone = new Bone();
        this.boneCount = 0;
        this.nameToAnimation = Collections.emptyMap();
    }

    Animator(Bone rootBone, int boneCount, Map<String, Animation> nameToAnimation) {
        this.rootBone = rootBone;
        this.boneCount = boneCount;
        this.nameToAnimation = nameToAnimation;
        this.currentAnimation = nameToAnimation.values().stream().findFirst().orElse(null);
    }

    public void play(String animationName) {
        this.animationTime = 0;
        this.currentAnimation = nameToAnimation.get(animationName);
    }


    public void update() {
        if (currentAnimation == null) {
            return;
        }
        animationTime += DISPLAY_MANAGER.getDeltaTime();
        if (animationTime > currentAnimation.getLength()) {
            this.animationTime %= currentAnimation.getLength();
        }
        Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
        applyPoseToBones(currentPose, rootBone, new Matrix4f());
    }

    private Map<String, Matrix4f> calculateCurrentAnimationPose() {
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
        float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
        float currentTime = animationTime - previousFrame.getTimeStamp();
        float progression = currentTime / totalTime;
        Map<String, Matrix4f> currentPose = new HashMap<>();
        for (String boneName : previousFrame.getBoneKeyFrames().keySet()) {
            BoneTransform previousTransform = previousFrame.getBoneKeyFrames().get(boneName);
            BoneTransform nextTransform = nextFrame.getBoneKeyFrames().get(boneName);
            BoneTransform currentTransform = BoneTransform.interpolate(previousTransform, nextTransform, progression);
            currentPose.put(boneName, currentTransform.getLocalTransform());
        }
        return currentPose;
    }

    private void applyPoseToBones(Map<String, Matrix4f> currentPose, Bone bone, Matrix4f parentTransform) {
        Matrix4f currentLocalTransform = currentPose.getOrDefault(bone.getName(), new Matrix4f());
        Matrix4f currentTransform = parentTransform.mul(currentLocalTransform, new Matrix4f());
        for (Bone childBone : bone.getChildren()) {
            applyPoseToBones(currentPose, childBone, currentTransform);
        }
        currentTransform.mul(bone.getInverseBindTransform(), currentTransform);
        bone.setAnimatedTransform(currentTransform);
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
