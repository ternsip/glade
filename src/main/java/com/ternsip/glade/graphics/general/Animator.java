package com.ternsip.glade.graphics.general;

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

    private static int UPDATE_INTERVAL_MILLISECONDS = 10;

    private final Model model;
    private AnimationFrames currentAnimationFrames;

    // TODO add MIN VALUE = EPS 1f-6
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
        float animationTimeDeltaSeconds = (System.currentTimeMillis() - animationStartMillis) / 1000f;
        float animationTime = animationTimeDeltaSeconds % currentAnimationFrames.getLengthSeconds();
        KeyFrame[] allFrames = getCurrentAnimationFrames().getKeyFrames();
        float duration = getCurrentAnimationFrames().getLengthSeconds();
        int frameNumber = allFrames.length;
        float deltaTime = frameNumber == 1 ?  duration : (duration / (frameNumber - 1));
        int frameIndex = (int)(animationTime / deltaTime);
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
