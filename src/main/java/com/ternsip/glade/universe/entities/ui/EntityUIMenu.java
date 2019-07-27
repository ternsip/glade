package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.universe.audio.Sound;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.util.Arrays;

@Getter
@Setter
public class EntityUIMenu extends EntityUI {

    private final EntityUITextButton button;

    private boolean active = true;

    public EntityUIMenu(boolean useAspect) {
        super(useAspect);

        this.button = new EntityUITextButton(
                new File("tools/button.png"),
                new File("tools/browse_overlay.png"),
                new File("tools/press_overlay.png"),
                new File("fonts/default.png"),
                new Vector4f(1, 1, 1, 1),
                "Exit",
                true
        );
        this.button.setScale(new Vector3f(0.1f, 0.05f, 1));
        this.button.setPosition(new Vector3f(0, -0.5f, 0));
        this.button.getOnPress().add(() -> new Sound(new File("sounds/click2.ogg")).register());
        this.button.getOnClick().add(() -> getUniverse().setActive(false));

        EntityUIEditBox editBox = new EntityUIEditBox(new File("tools/button.png"), new File("tools/editbox_frame.png"), new File("tools/editbox_pointer.png"), new File("fonts/default.png"), new Vector4f(1, 1, 1, 1), true);
        editBox.setScale(new Vector3f(0.2f, 0.05f, 1));
        editBox.setPosition(new Vector3f(0, 0.5f, 0));
        //editBox.register();

        EntityUIRadioBox radioBox = new EntityUIRadioBox(
                new File("tools/ui_background.png"),
                new File("tools/browse_overlay.png"),
                new File("tools/press_overlay.png"),
                new File("tools/checkbox_on.png"),
                new File("tools/checkbox_off.png"),
                new File("fonts/default.png"),
                new Vector4f(1, 1, 1, 1),
                Arrays.asList(
                        new EntityUICheckBox.Sign("trulala", state -> {}),
                        new EntityUICheckBox.Sign("abc", state -> {}),
                        new EntityUICheckBox.Sign("", state -> {}),
                        new EntityUICheckBox.Sign("test", state -> {})
                ),
                true
        );
        radioBox.setScale(new Vector3f(0.2f, 0.2f, 1));
        radioBox.setPosition(new Vector3f(0, 0, 0));
        //radioBox.register();

        EntityUIComboBox comboBox = new EntityUIComboBox(
                new File("tools/combo_drop.png"),
                new File("tools/browse_overlay.png"),
                new File("tools/press_overlay.png"),
                new File("tools/combo_background.jpg"),
                new File("tools/browse_overlay.png"),
                new File("tools/press_overlay.png"),
                new File("fonts/default.png"),
                new Vector4f(1, 1, 1, 1),
                Arrays.asList(
                        "abc",
                        "abc2",
                        "abc3"
                ),
                true
        );
        comboBox.setScale(new Vector3f(0.2f, 0.025f, 1));
        comboBox.setPosition(new Vector3f(0, 0, 0));
        //comboBox.register();

        EntityUIScrollbar scrollbar = new EntityUIScrollbar(
                new File("tools/scrollbar_background.jpg"),
                new File("tools/scrollbar_bar.png"),
                new File("tools/browse_overlay.png"),
                new File("tools/press_overlay.png"),
                new File("tools/scrollbar_up.png"),
                new File("tools/browse_overlay.png"),
                new File("tools/press_overlay.png"),
                state -> {},
                true
        );
        scrollbar.setScale(new Vector3f(0.025f, 0.2f, 1));
        scrollbar.setPosition(new Vector3f(0, 0, 0));
        //scrollbar.register();

    }

    @Override
    public void register() {
        super.register();
        getButton().register();
    }

    @Override
    public void unregister() {
        super.unregister();
        getButton().unregister();
    }

    public void toggle() {
        setActive(!isActive());
        if (isActive()) {
            register();
        } else {
            unregister();
        }
    }
}
