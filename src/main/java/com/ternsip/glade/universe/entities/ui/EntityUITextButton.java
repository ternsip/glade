package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.impl.EntityDynamicText2D;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4fc;

import java.io.File;

@Getter
@Setter
public class EntityUITextButton extends EntityUIButton {

    private final EntityDynamicText2D sign;

    private float textCompression = 0.8f;

    public EntityUITextButton(File background, File font, Vector4fc textColor, String text, boolean useAspect) {
        super(background, useAspect);
        this.sign = new EntityDynamicText2D(font, text, textColor, useAspect);
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
    public void update(EffigySprite effigy) {
        super.update(effigy);
        float textScale = 2f / Math.max(1, getSign().getText().length());
        getSign().setScale(new Vector3f(getVisualScale()).mul(textScale, textScale, 1));
        getSign().setRotation(getVisualRotation());
        getSign().setPosition(new Vector3f(getPosition()).add(0, 0, -0.01f));
        getSign().setVisible(isVisible());
        getSign().setTextCompression(getTextCompression());
    }

}
