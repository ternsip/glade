package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.io.File;

@Getter
@Setter
public class EntityUIScrollbar extends EntityUI {

    private final float BUTTON_SCALE_FACTOR_Y = 0.1f;
    private final float BUTTON_POSITION_STEP = 0.15f;

    private final Callback<CursorPosEvent> cursorPosCallback = this::trackCursor;

    private final EntitySprite background;
    private final EntityUIButton slider;
    private final EntityUIButton buttonUp;
    private final EntityUIButton buttonDown;

    private float spaceFactor = 0.5f;
    private float positionFactor = 0.5f;
    private float lastCursorY = 0;
    private boolean holding = false;

    public EntityUIScrollbar(
            File background,
            File sliderBackground,
            File sliderBrowseOverlay,
            File sliderPressOverlay,
            File buttonUpBackground,
            File buttonUpBrowseOverlay,
            File buttonUpPressOverlay,
            boolean useAspect
    ) {
        super(useAspect);
        this.background = new EntitySprite(background, true, useAspect);
        this.slider = new EntityUIButton(sliderBackground, sliderBrowseOverlay, sliderPressOverlay, useAspect);
        this.buttonUp = new EntityUIButton(buttonUpBackground, buttonUpBrowseOverlay, buttonUpPressOverlay, useAspect);
        this.buttonDown = new EntityUIButton(buttonUpBackground, buttonUpBrowseOverlay, buttonUpPressOverlay, useAspect);
        this.slider.getOnPress().add(() -> setHolding(true));
        this.slider.getOnRelease().add(() -> setHolding(false));
        this.buttonUp.getOnPress().add(() -> {
            setPositionFactor(Math.min(1, Math.max(0, getPositionFactor() - BUTTON_POSITION_STEP)));
        });
        this.buttonDown.getOnPress().add(() -> {
            setPositionFactor(Math.min(1, Math.max(0, getPositionFactor() + BUTTON_POSITION_STEP)));
        });
    }

    @Override
    public void register() {
        super.register();
        getBackground().register();
        getSlider().register();
        getButtonUp().register();
        getButtonDown().register();
        getBackground().register();
        getUniverse().getEventSnapReceiver().registerCallback(CursorPosEvent.class, getCursorPosCallback());
    }

    @Override
    public void unregister() {
        super.unregister();
        getBackground().unregister();
        getSlider().unregister();
        getButtonUp().unregister();
        getButtonDown().unregister();
        getBackground().unregister();
        getUniverse().getEventSnapReceiver().unregisterCallback(CursorPosEvent.class, getCursorPosCallback());
    }

    @Override
    public void update(EffigySprite effigy) {
        super.update(effigy);

        float sliderSpace = 1 - BUTTON_SCALE_FACTOR_Y * 2;
        float sliderScaleY = getScale().y() * sliderSpace;
        float sliderTrueScaleY = sliderScaleY * getSpaceFactor();
        float sliderDiffScale = sliderScaleY - sliderTrueScaleY;
        if (isHolding()) {
            setPositionFactor(Math.min(1, Math.max(0, (1 + (getPosition().y() - getLastCursorY()) / (getRatioY() * sliderDiffScale)) * 0.5f)));
        }
        float sliderOffset = sliderDiffScale * (1 - 2 * getPositionFactor());
        float buttonUpScaleY = getScale().y() * BUTTON_SCALE_FACTOR_Y;
        float buttonUpOffsetY = sliderScaleY + buttonUpScaleY;

        getBackground().setScale(getScale());
        getBackground().setPosition(getPosition());
        getBackground().setRotation(getRotation());
        getBackground().setVisible(isVisible());

        getSlider().setScale(new Vector3f(getScale().x(), sliderTrueScaleY, getScale().z()));
        getSlider().setPosition(new Vector3f(getPosition().x(), getPosition().y() + sliderOffset * getRatioY(), getPosition().z() - 0.01f));
        getSlider().setRotation(getRotation());
        getSlider().setVisible(isVisible());

        getButtonUp().setScale(new Vector3f(getScale().x(), buttonUpScaleY, getScale().z()));
        getButtonUp().setPosition(new Vector3f(getPosition().x(), getPosition().y() + buttonUpOffsetY * getRatioY(), getPosition().z() - 0.01f));
        getButtonUp().setRotation(getRotation());
        getButtonUp().setVisible(isVisible());

        getButtonDown().setScale(new Vector3f(getScale().x(), buttonUpScaleY, getScale().z()));
        getButtonDown().setPosition(new Vector3f(getPosition().x(), getPosition().y() - buttonUpOffsetY * getRatioY(), getPosition().z() - 0.01f));
        getButtonDown().setRotation(new Vector3f(getRotation().x(), getRotation().y(), getRotation().z() + (float)Math.PI));
        getButtonDown().setVisible(isVisible());
    }

    private void trackCursor(CursorPosEvent event) {
        setLastCursorY((float)event.getNormalY());
    }

}
