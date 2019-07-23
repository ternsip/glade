package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.impl.EntityDynamicText2D;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4fc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class EntityUICheckbox extends EntityUI {

    private final EntitySprite background;
    private final ArrayList<Bar> bars;

    public EntityUICheckbox(
            File background,
            File browseOverlay,
            File pressOverlay,
            File checkedImage,
            File uncheckedImage,
            File font,
            Vector4fc textColor,
            List<String> signs,
            boolean useAspect
    ) {
        super(useAspect);
        this.bars = signs.stream().map(sign -> new Bar(
                new EntityDynamicText2D(font, sign, textColor, useAspect),
                new EntityUIButton(uncheckedImage, browseOverlay, pressOverlay, useAspect)
        )).collect(Collectors.toCollection(ArrayList::new));
        this.background = new EntitySprite(background, true, useAspect);
    }

    @Override
    public void register() {
        super.register();
        getBars().forEach(bar -> {
            bar.getButton().register();
            bar.getSign().register();
        });
        getBackground().register();
    }

    @Override
    public void unregister() {
        super.unregister();
        getBars().forEach(bar -> {
            bar.getButton().unregister();
            bar.getSign().unregister();
        });
        getBackground().unregister();
    }

    @Override
    public void update(EffigySprite effigy) {
        super.update(effigy);

        Integer biggestTextLength = getBars().stream().mapToInt(v -> v.getSign().getText().length()).max().orElse(1);
        float textScaleX = getScale().x() / Math.max(1, biggestTextLength + 1);
        float textScaleY = getScale().y() / Math.max(1, getBars().size());

        getBackground().setScale(getScale());
        getBackground().setPosition(getPosition());
        getBackground().setRotation(getRotation());
        getBackground().setVisible(isVisible());

        for (int i = 0; i < getBars().size(); ++i) {

            float rowOffsetY = -textScaleY + (getBars().size() - i - 2) * textScaleY * 2;
            EntityDynamicText2D sign = getBars().get(i).getSign();
            EntityUIButton button = getBars().get(i).getButton();

            button.setScale(new Vector3f(textScaleX, textScaleY, 1));
            button.setRotation(getVisualRotation());
            button.setPosition(new Vector3f(getPosition()).add((-getScale().x() + textScaleX) * getRatioX(), rowOffsetY * getRatioY(), -0.01f));
            button.setVisible(isVisible());

            sign.setScale(new Vector3f(textScaleX, textScaleY, 1));
            sign.setRotation(getVisualRotation());
            sign.setPosition(new Vector3f(getPosition()).add((-getScale().x() + 2 * textScaleX) * getRatioX(), rowOffsetY * getRatioY(), -0.01f));
            sign.setVisible(isVisible());
            sign.setShiftX(true);
        }

    }

    @RequiredArgsConstructor
    @Getter
    private static class Bar {

        private final EntityDynamicText2D sign;
        private final EntityUIButton button;

    }

}
