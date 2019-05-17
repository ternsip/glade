package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Animator {

    private static int UPDATE_INTERVAL_MILLISECONDS = 10;

    private final Model model;
    private AnimationFrames currentAnimationFrames;
    private long animationStartMillis;
    private Matrix4f[] boneTransforms;
    private long lastUpdateMillis;

    public Animator(Model model) {
        this.model = model;
        this.currentAnimationFrames = model.getAnimation().getNameToAnimation().values().stream().findFirst().orElse(null);
        this.animationStartMillis = System.currentTimeMillis();
        this.boneTransforms = new Matrix4f[0];
    }

    public void play(String animationName) {
        this.animationStartMillis = 0;
        this.currentAnimationFrames = model.getAnimation().getNameToAnimation().get(animationName);
    }

    public Matrix4f[] getBoneTransforms() {
        if (currentAnimationFrames == null || currentAnimationFrames.getKeyFrames().length == 0) {
            return boneTransforms;
        }
        if (lastUpdateMillis + UPDATE_INTERVAL_MILLISECONDS < System.currentTimeMillis()) {
            lastUpdateMillis = System.currentTimeMillis();
            update();
        }
        return boneTransforms;
    }

    private void update() {
        setBoneTransforms(calcBoneTransforms());
    }

    private Matrix4f[] calcBoneTransforms() {
        Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
        Matrix4f[] boneMatrices = new Matrix4f[getModel().getAnimation().getBiggestBoneIndex() + 1];
        Animation.BoneIndexData[] boneIndexData = getModel().getAnimation().getBoneIndexDataTopologicallySorted();
        Matrix4f[] parentTransforms = new Matrix4f[boneIndexData.length];
        for (int i = 0; i < boneIndexData.length; ++i) {
            Bone bone = boneIndexData[i].getBone();
            int parentBoneOrder = boneIndexData[i].getParentBoneOrder();
            Matrix4f parentTransform = parentBoneOrder < 0 ? new Matrix4f() : parentTransforms[parentBoneOrder];
            Matrix4f currentLocalTransform = currentPose.getOrDefault(bone.getName(), new Matrix4f());
            Matrix4f currentTransform = parentTransform.mul(currentLocalTransform, new Matrix4f());
            parentTransforms[i] = new Matrix4f(currentTransform);
            if (bone.getIndex() >= 0) {
                currentTransform.mul(bone.getInverseBindTransform(), currentTransform);
                boneMatrices[bone.getIndex()] = currentTransform;
            }
        }
        return boneMatrices;
    }

    private Map<String, Matrix4f> calculateCurrentAnimationPose() {
        float animationTimeDeltaSeconds = (System.currentTimeMillis() - animationStartMillis) / 1000f;
        float animationTime = animationTimeDeltaSeconds % currentAnimationFrames.getLengthSeconds();
        KeyFrame[] allFrames = getCurrentAnimationFrames().getKeyFrames();
        float duration = Math.max(0.1f, getCurrentAnimationFrames().getLengthSeconds());
        int frameNumber = allFrames.length;
        float deltaTime = frameNumber == 1 ? duration : (duration / (frameNumber - 1));
        int frameIndex = Math.max(0, (int) (animationTime / deltaTime));
        KeyFrame currentFrame = allFrames[frameIndex];
        KeyFrame nextFrame = allFrames[(frameIndex + 1) % frameNumber];
        float progression = (animationTime % deltaTime) / deltaTime;
        Map<String, Matrix4f> currentPose = new HashMap<>();
        for (String boneName : currentFrame.getBoneKeyFrames().keySet()) {
            BoneTransform previousTransform = currentFrame.getBoneKeyFrames().get(boneName);
            BoneTransform nextTransform = nextFrame.getBoneKeyFrames().get(boneName);
            BoneTransform currentTransform = BoneTransform.interpolate(previousTransform, nextTransform, progression);
            currentPose.put(boneName, currentTransform.getLocalTransform());
        }
        return currentPose;
    }

}
