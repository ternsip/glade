package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.graphics.visual.base.Visual;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Getter
@Setter
public class DynamicReloadable extends DynamicVisual {

    private final Supplier<Visual> loadVisual;

    public DynamicReloadable(Supplier<Visual> loadVisual) {
        this.loadVisual = loadVisual;
        createVisuals();
    }

    @Override
    public List<Visual> createVisuals() {
        return Collections.singletonList(loadVisual.get());
    }

}
