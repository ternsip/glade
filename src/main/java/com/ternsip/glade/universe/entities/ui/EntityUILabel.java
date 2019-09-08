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
    }

    public String getText() {
        return getSign().getText();
    }

    public void setText(String text) {
        getSign().setText(text);
    }

    @Override
    public void register() {
        super.register();
        getSign().register();
    }

    @Override
    public void unregister() {
        super.unregister();
        getSign().unregister();
    }

    @Override
    public void clientUpdate() {
        super.clientUpdate();
        getSign().setScale(getVisualScale());
        getSign().setRotation(getVisualRotation());
        getSign().setPosition(getVisualPosition());
        getSign().setVisible(isVisible());
    }
}
