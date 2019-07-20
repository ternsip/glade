package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.universe.entities.impl.EntityDynamicText2D;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4fc;

import java.io.File;

@Getter
@Setter
public class EntityUILabel extends EntityUI {

    private final EntityDynamicText2D sign;

    public EntityUILabel(File font, Vector4fc textColor, String text, boolean useAspect) {
        super(useAspect);
        this.sign = new EntityDynamicText2D(font, text, textColor, useAspect);
        this.sign.register();
    }

    @Override
    public void finish() {
        super.finish();
        getSign().finish();
    }

    @Override
    public void update() {
        super.update();
        getSign().setScale(getVisualScale());
        getSign().setRotation(getVisualRotation());
        getSign().setPosition(getVisualPosition());
        getSign().setVisible(isVisible());
    }

}
