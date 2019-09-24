package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

@Getter
@Setter
public class EntityUI extends GraphicalEntity<EffigySprite> {

    private boolean useAspect;
    private float ratioX = 1;
    private float ratioY = 1;
    private Vector2fc uiCenter = new Vector2f(0);
    private Vector2fc uiSize = new Vector2f(1);

    public EntityUI(boolean useAspect) {
        this.useAspect = useAspect;
    }

    @Override
    public void update(EffigySprite effigy) {
        setRatioX(effigy.getRatioX());
        setRatioY(effigy.getRatioY());
    }

    @Override
    public EffigySprite getEffigy() {
        EffigySprite effigySprite = new EffigySprite(null, true, true);
        effigySprite.setVisible(false);
        return effigySprite;
    }

    @Override
    public void setPosition(Vector3fc position) {
        super.setPosition(position);
        setUiCenter(new Vector2f(getPosition().x(), getPosition().y()));
    }

    @Override
    public void setScale(Vector3fc scale) {
        super.setScale(scale);
        setUiSize(new Vector2f(getScale().x(), getScale().y()));
    }

    public Vector3fc getVisualScale() {
        return getScale();
    }

    public Vector3fc getVisualPosition() {
        return getPosition();
    }

    public Vector3fc getVisualRotation() {
        return getRotation();
    }

    public boolean isInside(float normalizedX, float normalizedY) {
        float sx = getUiCenter().x() - getUiSize().x() * getRatioX();
        float sy = getUiCenter().y() - getUiSize().y() * getRatioY();
        float ex = getUiCenter().x() + getUiSize().x() * getRatioX();
        float ey = getUiCenter().y() + getUiSize().y() * getRatioY();
        return sx <= normalizedX && sy <= normalizedY && ex >= normalizedX && ey >= normalizedY;
    }

}
