package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Animation {

    private final double length; // In seconds
    private final KeyFrame[] keyFrames;

}
