package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.graphics.visual.base.Visual;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class DynamicVisual implements Visual {

    private final List<Visual> visuals = new ArrayList<>();

    public void reload() {
        finish();
        getVisuals().addAll(createVisuals());
    }

    public abstract List<Visual> createVisuals();

    @Override
    public void finish() {
        for (Visual visual : getVisuals()) {
            visual.finish();
        }
        getVisuals().clear();
    }

}
