package com.ternsip.glade.graphics.entities.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class MultiEntity implements Figure {

    private final BaseFigure[] baseFigures;

    public void finish() {
        for (Figure figure : getBaseFigures()) {
            figure.finish();
        }
    }

}
