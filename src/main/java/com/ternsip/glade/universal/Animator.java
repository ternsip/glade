package com.ternsip.glade.universal;

import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.*;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@Getter
@Setter
public class Animator {

    private final Bone rootBone;
    private final Map<String, Animation> nameToAnimation;
    private final int biggestBoneIndex;

    private Animation currentAnimation;
    private float animationTime = 0;

    public Animator() {
        this(new Bone(), Collections.emptyMap());
    }

    Animator(Bone rootBone, Map<String, Animation> nameToAnimation) {
        this.rootBone = rootBone;
        this.biggestBoneIndex = calcBiggestBoneIndex(rootBone);
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
    }

    private static int calcBiggestBoneIndex(Bone rootBone) {
        Stack<Bone> bonesStack = new Stack<>();
        bonesStack.push(rootBone);
        int biggestBoneIndex = 0;
        while (!bonesStack.isEmpty()) {
            Bone topBone = bonesStack.pop();
            bonesStack.addAll(topBone.getChildren());
            biggestBoneIndex = Math.max(biggestBoneIndex, topBone.getIndex());
        }
        return biggestBoneIndex;
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

    public Matrix4f[] getBoneTransforms() {
        if (currentAnimation == null) {
            return new Matrix4f[0];
        }
        Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
        Stack<Map.Entry<Bone, Matrix4f>> dfsStack = new Stack<>();
        dfsStack.push(new AbstractMap.SimpleEntry<>(rootBone, new Matrix4f()));
        Matrix4f[] boneMatrices = new Matrix4f[biggestBoneIndex + 1];
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

}
