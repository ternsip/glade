package com.ternsip.glade.graphics.visual.base.graphical;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class MultiVisual implements Visual {

    private final Visual[] visuals;

    public void finish() {
        for (Visual visual : getVisuals()) {
            visual.finish();
        }
    }

}
