package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.universe.entities.impl.EntityDynamicText2D;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.io.File;

@Getter
@Setter
public class EntityUITextButton extends EntityUIButton {

    private static final float TEXT_SCALE_Y = 0.8f;

    private final EntityDynamicText2D sign;

    private boolean fit = false;

    public EntityUITextButton(File background, File browseOverlay, File pressOverlay, File font, Vector4fc textColor, String text, boolean useAspect) {
        super(background, browseOverlay, pressOverlay, useAspect);
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

        Vector3fc scale = getVisualScale();
        Vector3fc position = getVisualPosition();
        Vector3fc rotation = getVisualRotation();

        float textScale = isFit() ? (scale.y() * 2f / Math.max(1, getSign().getText().length())) : scale.y() * TEXT_SCALE_Y;
        getSign().setScale(new Vector3f(textScale, textScale, 1));
        getSign().setRotation(rotation);
        getSign().setPosition(new Vector3f(position.x(), position.y(), position.z() - 0.01f));
        getSign().setVisible(isVisible());
    }

}
