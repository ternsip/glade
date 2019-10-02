package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4fc;

@Getter
@Setter
public class Animation {

    private final Model model;
    private AnimationTrack animationTrack;

    public Animation(Model model) {
        this.model = model;
        this.animationTrack = model.getAnimationData().getAnimationTrack("");
    }

    public void play(String animationName) {
        this.animationTrack = getModel().getAnimationData().getAnimationTrack(animationName);
    }

    public boolean isAnimated() {
        return !getAnimationTrack().isEmpty();
    }

    public Matrix4fc[] calcBoneTransforms() {
        if (!isAnimated()) {
            return new Matrix4fc[0];
        }
        return getModel().getAnimationData().calcBoneTransforms(getAnimationTrack());
    }

}
