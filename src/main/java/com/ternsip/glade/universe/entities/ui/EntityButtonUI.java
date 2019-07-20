package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.graphics.visual.impl.basis.EffigyUIButton;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.File;

@Getter
public class EntityButtonUI extends Entity<EffigyUIButton> {

    private static final long ANIMATION_TIME_MILLISECONDS = 2000;

    private final File file;
    private final boolean useAspect;

    public EntityButtonUI(File file, boolean useAspect, Vector3fc scale, Vector3fc position, Vector3fc rotation) {
        this.file = file;
        this.useAspect = useAspect;
        setScale(scale);
        setPosition(position);
        setRotation(rotation);
    }

    @Override
    public EffigyUIButton getEffigy() {
        return new EffigyUIButton(getFile(), isUseAspect());
    }

    @Override
    public void update(EffigyUIButton effigy) {
        if (effigy.isCursorInside()) {
            float phase = (System.currentTimeMillis() % ANIMATION_TIME_MILLISECONDS) / (float)ANIMATION_TIME_MILLISECONDS;
            float scaleCriteria = (float) (1 + Math.abs(Math.cos(2 * Math.PI * phase))) * 0.5f;
            float rotationCriteria = 0;
            effigy.setScale(new Vector3f(getScale().x() * scaleCriteria, getScale().y() * scaleCriteria, getScale().z()));
            effigy.setRotation(new Vector3f(getRotation().x(), getRotation().y(), getRotation().z() + rotationCriteria));
        } else {
            effigy.setScale(getScale());
            effigy.setRotation(getRotation());
        }
        effigy.setPosition(getPosition());
        effigy.setVisible(isVisible());
        effigy.setUiCenter(new Vector2f(getPosition().x(), getPosition().y()));
        effigy.setUiSize(new Vector2f(getScale().x(), getScale().y()));
    }
}
