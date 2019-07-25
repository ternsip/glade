package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.universe.entities.impl.EntityDynamicText2D;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
public class EntityUICheckBox extends EntityUI {

    private final EntitySprite background;
    private final ArrayList<Bar> bars;

    public EntityUICheckBox(
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
        super(useAspect);
        this.bars = signs.stream().map(sign -> {
            EntityDynamicText2D text2D = new EntityDynamicText2D(font, sign.getText(), textColor, useAspect);
            EntityUISwitcher switcher = new EntityUISwitcher(uncheckedImage, browseOverlay, pressOverlay, checkedImage, useAspect);
            switcher.getOnClick().add(() -> sign.getOnSwitch().accept(switcher.isSwitched()));
            return new Bar(text2D, switcher);
        }).collect(Collectors.toCollection(ArrayList::new));
        this.background = new EntitySprite(background, true, useAspect);
    }

    @Override
    public void register() {
        super.register();
        getBars().forEach(bar -> {
            bar.getSwitcher().register();
            bar.getSign().register();
        });
        getBackground().register();
    }

    @Override
    public void unregister() {
        super.unregister();
        getBars().forEach(bar -> {
            bar.getSwitcher().unregister();
            bar.getSign().unregister();
        });
        getBackground().unregister();
    }

    @Override
    public void update() {
        super.update();

        Vector3fc scale = getVisualScale();
        Vector3fc position = getVisualPosition();
        Vector3fc rotation = getVisualRotation();
        Integer biggestTextLength = getBars().stream().mapToInt(v -> v.getSign().getText().length()).max().orElse(1);
        float textScaleX = scale.x() / Math.max(1, biggestTextLength + 1);
        float textScaleY = scale.y() / Math.max(1, getBars().size());

        getBackground().setScale(scale);
        getBackground().setPosition(position);
        getBackground().setRotation(rotation);
        getBackground().setVisible(isVisible());

        for (int i = 0; i < getBars().size(); ++i) {

            float rowOffsetY = -textScaleY + (getBars().size() - i - 2) * textScaleY * 2;
            EntityDynamicText2D sign = getBars().get(i).getSign();
            EntityUIButton button = getBars().get(i).getSwitcher();

            button.setScale(new Vector3f(textScaleX, textScaleY, 1));
            button.setRotation(rotation);
            button.setPosition(new Vector3f(position).add((-scale.x() + textScaleX) * getRatioX(), rowOffsetY * getRatioY(), -0.01f));
            button.setVisible(isVisible());

            sign.setScale(new Vector3f(textScaleX, textScaleY, 1));
            sign.setRotation(rotation);
            sign.setPosition(new Vector3f(position).add((-scale.x() + 2 * textScaleX) * getRatioX(), rowOffsetY * getRatioY(), -0.01f));
            sign.setVisible(isVisible());
            sign.setShiftX(true);
        }

    }

    @RequiredArgsConstructor
    @Getter
    public static class Bar {

        private final EntityDynamicText2D sign;
        private final EntityUISwitcher switcher;

    }

    @RequiredArgsConstructor
    @Getter
    public static class Sign {

        private final String text;
        private final Consumer<Boolean> onSwitch;

    }

}
