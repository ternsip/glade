package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class EntityUISwitcher extends EntityUIButton {

    private final EntitySprite switchedOverlay;

    private boolean switched = false;

    public EntityUISwitcher(File background, File browseOverlay, File pressBackground, File switchedOverlay, boolean useAspect) {
        super(background, browseOverlay, pressBackground, useAspect);
        getOnClick().add(() -> switched = !switched);
        this.switchedOverlay = new EntitySprite(switchedOverlay, true, useAspect);
    }

    @Override
    public void register() {
        super.register();
        getSwitchedOverlay().register();
    }

    @Override
    public void unregister() {
        super.unregister();
        getSwitchedOverlay().unregister();
    }

    @Override
    public void update() {
        super.update();
        getSwitchedOverlay().setScale(getVisualScale());
        getSwitchedOverlay().setRotation(getVisualRotation());
        getSwitchedOverlay().setPosition(getPosition());
        getSwitchedOverlay().setVisible(isVisible() && isSwitched());
        getBackground().setVisible(isVisible());
    }
}
