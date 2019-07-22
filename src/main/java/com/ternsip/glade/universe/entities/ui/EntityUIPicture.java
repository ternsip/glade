package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
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
    }

    @Override
    public void register() {
        super.register();
        getPicture().register();
    }

    @Override
    public void unregister() {
        super.unregister();
        getPicture().unregister();
    }

    @Override
    public void update(EffigySprite effigy) {
        super.update(effigy);
        getPicture().setScale(getVisualScale());
        getPicture().setRotation(getVisualRotation());
        getPicture().setPosition(getVisualPosition());
        getPicture().setVisible(isVisible());
    }
}
