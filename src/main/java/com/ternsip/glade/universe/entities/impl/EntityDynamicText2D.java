package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigyDynamicText;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4fc;

import java.io.File;

@Getter
@Setter
public class EntityDynamicText2D extends Entity<EffigyDynamicText> {

    private final File file;
    private final Vector4fc color;
    private String text;
    private boolean useAspect;
    private boolean shiftX = false;
    private boolean shiftY = false;
    private float textCompression = 0.8f;

    public EntityDynamicText2D(File file, String text, Vector4fc color, boolean useAspect) {
        this.file = file;
        this.text = text;
        this.color = color;
        this.useAspect = useAspect;
    }

    @Override
    public void update(EffigyDynamicText effigy) {
        super.update(effigy);
        effigy.setShiftX(isShiftX());
        effigy.setShiftY(isShiftY());
        effigy.setTextCompression(getTextCompression());
        effigy.setText(getText());
    }

    @Override
    public EffigyDynamicText getEffigy() {
        return new EffigyDynamicText(getFile(), true, isUseAspect(), getColor(), getText());
    }
}
