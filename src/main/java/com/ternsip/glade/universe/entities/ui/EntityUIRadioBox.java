package com.ternsip.glade.universe.entities.ui;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4fc;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class EntityUIRadioBox extends EntityUICheckBox {


    public EntityUIRadioBox(
            File background,
            File browseOverlay,
            File pressOverlay,
            File checkedImage,
            File uncheckedImage,
            File font,
            Vector4fc textColor,
            List<Sign> signs,
            boolean useAspect
    ) {
        super(background, browseOverlay, pressOverlay, checkedImage, uncheckedImage, font, textColor, signs, useAspect);
        getBars().forEach(bar -> {
            bar.getSwitcher().getOnClick().add(() -> {
                getBars().forEach(anotherBar -> anotherBar.getSwitcher().setSwitched(false));
                bar.getSwitcher().setSwitched(true);
            });
        });
    }
}
