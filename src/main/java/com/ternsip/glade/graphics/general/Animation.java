package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

@Getter
@Setter
public class Animation {

    private final Model model;
    private AnimationTrack animationTrack;
    private Matrix4f[] boneTransforms;

    public Animation(Model model) {
        this.model = model;
        this.animationTrack = model.getAnimationData().getAnimationTrack("");
        this.boneTransforms = new Matrix4f[0];
    }

    public void play(String animationName) {
        this.animationTrack = getModel().getAnimationData().getAnimationTrack(animationName);
    }

    public void update() {
        if (getAnimationTrack() == null || getAnimationTrack().isEmpty()) {
            return;
        }
        setBoneTransforms(getModel().getAnimationData().calcBoneTransforms(getAnimationTrack()));
    }

    public boolean isAnimated() {
        return getBoneTransforms().length > 0;
    }

}
