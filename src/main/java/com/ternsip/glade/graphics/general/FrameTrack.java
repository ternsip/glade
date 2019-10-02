package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@NonNull
class FrameTrack {

    private final float lengthSeconds;
    private final KeyFrame[] keyFrames;

    FrameTrack() {
        this.lengthSeconds = 0;
        this.keyFrames = new KeyFrame[]{};
    }

}
