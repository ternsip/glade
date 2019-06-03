package com.ternsip.glade.universe.entities.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class MultiGraphical {

    private final Graphical[] graphicals;

    public void finish() {
        for (Graphical graphical : getGraphicals()) {
            graphical.finish();
        }
    }

}
