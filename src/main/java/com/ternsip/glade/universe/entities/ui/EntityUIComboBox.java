package com.ternsip.glade.universe.entities.ui;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class EntityUIComboBox extends EntityUI {

    private final EntityUIButton drop;
    private final ArrayList<EntityUITextButton> options;
    private final EntityUITextButton selected;

    private boolean opened = false;

    public EntityUIComboBox(
            File dropBackground,
            File dropBrowseOverlay,
            File dropPressOverlay,
            File selectBackground,
            File selectBrowseOverlay,
            File selectPressOverlay,
            File font,
            Vector4fc textColor,
            List<String> selections,
            boolean useAspect
    ) {
        super(useAspect);
        this.drop = new EntityUIButton(dropBackground, dropBrowseOverlay, dropPressOverlay, useAspect);
        this.drop.getOnClick().add(this::roll);
        this.options = selections.stream().map(text -> {
                    EntityUITextButton button = new EntityUITextButton(selectBackground, selectBrowseOverlay, selectPressOverlay, font, textColor, text, useAspect);
                    button.setAnimated(false);
                    button.getOnClick().add(() -> getSelected().setText(button.getText()));
                    button.getOnClick().add(this::roll);
                    return button;
                }
        ).collect(Collectors.toCollection(ArrayList::new));
        this.options.forEach(e -> e.setAnimated(false));
        String selectedText = this.options.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Combo box options should contain at least 1 element"))
                .getText();
        this.selected = new EntityUITextButton(selectBackground, selectBrowseOverlay, selectPressOverlay, font, textColor, selectedText, useAspect);
        this.selected.setAnimated(false);
        this.selected.getOnClick().add(this::roll);
    }

    @Override
    public void register() {
        super.register();
        getDrop().register();
        getSelected().register();
    }

    @Override
    public void unregister() {
        super.unregister();
        getDrop().unregister();
        getSelected().unregister();
        getOptions().forEach(EntityUIButton::unregister);
    }

    @Override
    public void clientUpdate() {
        super.clientUpdate();

        Vector3fc scale = getVisualScale();
        Vector3fc position = getVisualPosition();
        Vector3fc rotation = getVisualRotation();

        getDrop().setScale(new Vector3f(scale.y(), scale.y(), scale.z()));
        getDrop().setPosition(new Vector3f(position.x() + (scale.x() - scale.y()) * getRatioX(), position.y(), position.z()));
        getDrop().setRotation(rotation);
        getDrop().setVisible(isVisible());

        getSelected().setScale(new Vector3f(scale.x() - scale.y(), scale.y(), scale.z()));
        getSelected().setPosition(new Vector3f(position.x() - scale.y() * getRatioX(), position.y(), position.z()));
        getSelected().setRotation(rotation);
        getSelected().setVisible(isVisible());

        for (int i = 0; i < getOptions().size(); ++i) {

            EntityUITextButton button = getOptions().get(i);
            button.setScale(scale);
            button.setRotation(rotation);
            button.setPosition(new Vector3f(position.x(), position.y() - (i + 1) * scale.y() * 2 * getRatioY(), position.z()));
            button.setVisible(isVisible());

        }

    }

    public void roll() {
        if (isOpened()) {
            getOptions().forEach(EntityUIButton::unregister);
            setOpened(false);
        } else {
            getOptions().forEach(EntityUIButton::register);
            setOpened(true);
        }
    }

}
