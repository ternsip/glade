package com.ternsip.glade.universe.graphicals.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class MultiGraphical implements Visual {

    private final Graphical[] graphicals;

    @Override
    public void update() {
    }

    public void finish() {
        for (Graphical graphical : getGraphicals()) {
            graphical.finish();
        }
    }

}
