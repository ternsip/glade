package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class EntityUIPicture extends EntityUI {

    private final EntitySprite picture;

    public EntityUIPicture(File file, boolean useAspect) {
        super(useAspect);
        this.picture = new EntitySprite(file, true, useAspect);
        this.picture.register();
    }

    @Override
    public void finish() {
        super.finish();
        getPicture().finish();
    }

    @Override
    public void update() {
        super.update();
        getPicture().setScale(getVisualScale());
        getPicture().setRotation(getVisualRotation());
        getPicture().setPosition(getVisualPosition());
        getPicture().setVisible(isVisible());
    }
}
