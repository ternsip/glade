package com.ternsip.glade.universe.entities.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class MultiEntity {

    private final Entity[] entities;

    public void finish() {
        for (Entity entity : getEntities()) {
            entity.finish();
        }
    }

}
