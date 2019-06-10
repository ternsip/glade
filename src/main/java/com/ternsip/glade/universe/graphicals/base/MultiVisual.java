package com.ternsip.glade.universe.graphicals.base;

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
