package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.graphics.visual.impl.basis.EffigyUIButton;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntityDynamicText2D;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.io.File;

@Getter
public class EntityUIButton extends Entity<EffigyUIButton> {

    private static final long ANIMATION_TIME_MILLISECONDS = 2000;

    private final File file;
    private final boolean useAspect;
    private final EntityDynamicText2D sign;

    public EntityUIButton(File background, File font, Vector4fc textColor, boolean useAspect, String text) {
        this.file = background;
        this.useAspect = useAspect;
        this.sign = new EntityDynamicText2D(font, text, textColor);
        this.sign.register();
    }

    @Override
    public EffigyUIButton getEffigy() {
        return new EffigyUIButton(getFile(), isUseAspect());
    }

    @Override
    public void update(EffigyUIButton effigy) {
        Vector3fc visualScale = getVisualScale(effigy);
        float textScale = 2f / Math.max(1, getSign().getText().length());
        getSign().setScale(new Vector3f(visualScale).mul(textScale, textScale, 1));
        getSign().setRotation(getRotation());
        getSign().setPosition(new Vector3f(getPosition()).add(0, 0, -0.01f));
        getSign().setVisible(isVisible());
        effigy.setScale(visualScale);
        effigy.setRotation(getRotation());
        effigy.setPosition(getPosition());
        effigy.setVisible(isVisible());
        effigy.setUiCenter(new Vector2f(getPosition().x(), getPosition().y()));
        effigy.setUiSize(new Vector2f(getScale().x(), getScale().y()));
    }

    @Override
    public void finish() {
        super.finish();
        getSign().finish();
    }

    private Vector3fc getVisualScale(EffigyUIButton effigy) {
        if (!effigy.isCursorInside()) {
            return getScale();
        }
        float phase = (System.currentTimeMillis() % ANIMATION_TIME_MILLISECONDS) / (float)ANIMATION_TIME_MILLISECONDS;
        float scaleCriteria = (float) (1 + Math.abs(Math.cos(2 * Math.PI * phase))) * 0.5f;
        return new Vector3f(getScale().x() * scaleCriteria, getScale().y() * scaleCriteria, getScale().z());
    }

}
