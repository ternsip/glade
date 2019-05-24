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
    private long lastUpdateMillis;

    public Animation(Model model) {
        this.model = model;
        this.animationTrack = model.getAnimationTrack("");
        this.boneTransforms = new Matrix4f[0];
    }

    public void play(String animationName) {
        this.animationTrack = model.getAnimationTrack(animationName);
    }

    public void update(long updateIntervalMilliseconds) {
        if (getAnimationTrack() == null || getAnimationTrack().isEmpty()) {
            return;
        }
        if (getLastUpdateMillis() + updateIntervalMilliseconds < System.currentTimeMillis()) {
            setLastUpdateMillis(System.currentTimeMillis());
            setBoneTransforms(model.calcBoneTransforms(getAnimationTrack()));
        }
    }

}
