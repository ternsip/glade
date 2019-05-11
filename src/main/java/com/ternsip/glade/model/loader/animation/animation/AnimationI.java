package com.ternsip.glade.model.loader.animation.animation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AnimationI {

    private final double length; // In seconds
    private final KeyFrame[] keyFrames;

}
