package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Animation {

    private final Model model;
    private AnimationTrack animationTrack;

    public Animation(Model model) {
        this.model = model;
        this.animationTrack = getModel().getAnimationTrack("");
    }

    public void play(String animationName) {
        this.animationTrack = getModel().getAnimationTrack(animationName);
    }

    public boolean isAnimated() {
        return getModel().isAnimated() && !getAnimationTrack().isEmpty();
    }

}
