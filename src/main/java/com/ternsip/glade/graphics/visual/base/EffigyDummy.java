package com.ternsip.glade.graphics.visual.base;

import com.ternsip.glade.graphics.general.Model;
import lombok.Getter;

@Getter
public class EffigyDummy extends EffigyAnimated {

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public Model loadModel() {
        return Model.builder().build();
    }
}
