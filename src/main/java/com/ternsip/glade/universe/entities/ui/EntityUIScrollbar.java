package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.CursorPosEvent;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.File;
import java.util.function.Consumer;

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
    private final Consumer<Float> onSlide;

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
            Consumer<Float> onSlide,
            boolean useAspect
    ) {
        super(useAspect);
        this.background = new EntitySprite(background, true, useAspect);
        this.slider = new EntityUIButton(sliderBackground, sliderBrowseOverlay, sliderPressOverlay, useAspect);
        this.buttonUp = new EntityUIButton(buttonUpBackground, buttonUpBrowseOverlay, buttonUpPressOverlay, useAspect);
        this.buttonDown = new EntityUIButton(buttonUpBackground, buttonUpBrowseOverlay, buttonUpPressOverlay, useAspect);
        this.onSlide = onSlide;
        this.slider.getOnPress().add(() -> setHolding(true));
        this.slider.getOnRelease().add(() -> setHolding(false));
        this.slider.setAnimated(false);
        this.buttonUp.setAnimated(false);
        this.buttonDown.setAnimated(false);
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
        getUniverseClient().getEventSnapReceiver().registerCallback(CursorPosEvent.class, getCursorPosCallback());
    }

    @Override
    public void unregister() {
        super.unregister();
        getBackground().unregister();
        getSlider().unregister();
        getButtonUp().unregister();
        getButtonDown().unregister();
        getBackground().unregister();
        getUniverseClient().getEventSnapReceiver().unregisterCallback(CursorPosEvent.class, getCursorPosCallback());
    }

    @Override
    public void update() {
        super.update();

        Vector3fc scale = getVisualScale();
        Vector3fc position = getVisualPosition();
        Vector3fc rotation = getVisualRotation();

        float sliderSpace = 1 - BUTTON_SCALE_FACTOR_Y * 2;
        float sliderScaleY = scale.y() * sliderSpace;
        float sliderTrueScaleY = sliderScaleY * getSpaceFactor();
        float sliderDiffScale = sliderScaleY - sliderTrueScaleY;
        if (isHolding()) {
            setPositionFactor(Math.min(1, Math.max(0, (1 + (position.y() - getLastCursorY()) / (getRatioY() * sliderDiffScale)) * 0.5f)));
            getOnSlide().accept(getPositionFactor());
        }
        float sliderOffset = sliderDiffScale * (1 - 2 * getPositionFactor());
        float buttonUpScaleY = scale.y() * BUTTON_SCALE_FACTOR_Y;
        float buttonUpOffsetY = sliderScaleY + buttonUpScaleY;

        getBackground().setScale(scale);
        getBackground().setPosition(position);
        getBackground().setRotation(rotation);
        getBackground().setVisible(isVisible());

        getSlider().setScale(new Vector3f(scale.x(), sliderTrueScaleY, scale.z()));
        getSlider().setPosition(new Vector3f(position.x(), position.y() + sliderOffset * getRatioY(), position.z() - 0.01f));
        getSlider().setRotation(rotation);
        getSlider().setVisible(isVisible());

        getButtonUp().setScale(new Vector3f(scale.x(), buttonUpScaleY, scale.z()));
        getButtonUp().setPosition(new Vector3f(position.x(), position.y() + buttonUpOffsetY * getRatioY(), position.z() - 0.01f));
        getButtonUp().setRotation(rotation);
        getButtonUp().setVisible(isVisible());

        getButtonDown().setScale(new Vector3f(scale.x(), buttonUpScaleY, scale.z()));
        getButtonDown().setPosition(new Vector3f(position.x(), position.y() - buttonUpOffsetY * getRatioY(), position.z() - 0.01f));
        getButtonDown().setRotation(new Vector3f(rotation.x(), rotation.y(), rotation.z() + (float) Math.PI));
        getButtonDown().setVisible(isVisible());
    }

    private void trackCursor(CursorPosEvent event) {
        setLastCursorY((float) event.getNormalY());
    }

}
