package com.ternsip.glade.model.loader.animation.animation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Animation {

    private final double length; // In seconds
    private final KeyFrame[] keyFrames;

}
