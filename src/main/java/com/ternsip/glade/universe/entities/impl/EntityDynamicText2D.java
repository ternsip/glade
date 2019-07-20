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

    public EntityDynamicText2D(File file, String text, Vector4fc color) {
        this.file = file;
        this.text = text;
        this.color = color;
    }

    @Override
    public void update(EffigyDynamicText effigy) {
        super.update(effigy);
        effigy.setText(getText());
    }

    @Override
    public EffigyDynamicText getEffigy() {
        return new EffigyDynamicText(getFile(), true, true, getColor(), getText());
    }
}
