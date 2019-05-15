package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@Getter
@Setter
public class Animator {

    private final Model model;
    private AnimationFrames currentAnimationFrames;
    private float animationTime;

    public Animator(Model model) {
        this.model = model;
        this.currentAnimationFrames = model.getAnimation().getNameToAnimation().values().stream().findFirst().orElse(null);
        this.animationTime = 0;
    }

    public void play(String animationName) {
        this.animationTime = 0;
        this.currentAnimationFrames = model.getAnimation().getNameToAnimation().get(animationName);
    }

    public void update() {
        if (currentAnimationFrames == null) {
            return;
        }
        animationTime += DISPLAY_MANAGER.getDeltaTime();
        if (animationTime > currentAnimationFrames.getLength()) {
            this.animationTime %= currentAnimationFrames.getLength();
        }
    }

    public Matrix4f[] getBoneTransforms() {
        if (currentAnimationFrames == null) {
            return new Matrix4f[0];
        }
        Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
        Stack<Map.Entry<Bone, Matrix4f>> dfsStack = new Stack<>();
        dfsStack.push(new AbstractMap.SimpleEntry<>(getModel().getAnimation().getRootBone(), new Matrix4f()));
        Matrix4f[] boneMatrices = new Matrix4f[getModel().getAnimation().getBiggestBoneIndex() + 1];
        while (!dfsStack.isEmpty()) {
            Map.Entry<Bone, Matrix4f> topPath = dfsStack.pop();
            Bone bone = topPath.getKey();
            Matrix4f parentTransform = topPath.getValue();
            Matrix4f currentLocalTransform = currentPose.getOrDefault(bone.getName(), new Matrix4f());
            Matrix4f currentTransform = parentTransform.mul(currentLocalTransform, new Matrix4f());
            for (Bone childBone : bone.getChildren()) {
                dfsStack.add(new AbstractMap.SimpleEntry<>(childBone, new Matrix4f(currentTransform)));
            }
            currentTransform.mul(bone.getInverseBindTransform(), currentTransform);
            if (bone.getIndex() >= 0) {
                boneMatrices[bone.getIndex()] = currentTransform;
            }
        }
        return boneMatrices;
    }

    private Map<String, Matrix4f> calculateCurrentAnimationPose() {
        KeyFrame[] allFrames = currentAnimationFrames.getKeyFrames();
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

}
