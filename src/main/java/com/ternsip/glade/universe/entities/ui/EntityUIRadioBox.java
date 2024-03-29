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
            File pressBackground,
            File switchedOverlay,
            File switcherBackground,
            File font,
            Vector4fc textColor,
            List<Sign> signs,
            boolean useAspect
    ) {
        super(background, browseOverlay, pressBackground, switchedOverlay, switcherBackground, font, textColor, signs, useAspect);
        getBars().forEach(bar -> {
            bar.getSwitcher().getOnClick().add(() -> {
                getBars().forEach(anotherBar -> anotherBar.getSwitcher().setSwitched(false));
                bar.getSwitcher().setSwitched(true);
            });
        });
    }
}
