package com.ternsip.glade.universe.entities.ui;

import com.ternsip.glade.universe.audio.Sound;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.io.File;
import java.util.Arrays;

@Getter
@Setter
public class EntityUIMenu extends EntityUI {

    private static final File BUTTON_BACKGROUND = new File("interface/button.png");
    private static final File BROWSE_OVERLAY = new File("interface/browse_overlay.png");
    private static final File PRESS_OVERLAY = new File("interface/press_overlay.png");
    private static final File FONT = new File("fonts/default.png");
    private static final Vector4fc TEXT_COLOR = new Vector4f(1, 1, 1, 1);
    private static final File CLICK_SOUND = new File("sounds/click2.ogg");

    private final EntityUITextButton exitButton;
    private final EntityUITextButton resumeButton;
    private final EntityUITextButton connectButton;
    private final EntityUITextButton hostButton;
    private final EntityUITextButton optionsButton;

    private boolean active = false;

    public EntityUIMenu() {
        super(true);

        this.resumeButton = new EntityUITextButton(BUTTON_BACKGROUND, BROWSE_OVERLAY, PRESS_OVERLAY, FONT, TEXT_COLOR, "Resume", true);
        this.resumeButton.setScale(new Vector3f(0.1f, 0.05f, 1));
        this.resumeButton.setPosition(new Vector3f(0, 0.5f, 0));
        this.resumeButton.getOnPress().add(() -> new Sound(CLICK_SOUND).register());
        this.resumeButton.getOnClick().add(this::toggle);

        this.connectButton = new EntityUITextButton(BUTTON_BACKGROUND, BROWSE_OVERLAY, PRESS_OVERLAY, FONT, TEXT_COLOR, "Connect", true);
        this.connectButton.setScale(new Vector3f(0.1f, 0.05f, 1));
        this.connectButton.setPosition(new Vector3f(0, 0.25f, 0));
        this.connectButton.getOnPress().add(() -> new Sound(CLICK_SOUND).register());
        this.connectButton.getOnClick().add(() -> getUniverse().startClient());

        this.hostButton = new EntityUITextButton(BUTTON_BACKGROUND, BROWSE_OVERLAY, PRESS_OVERLAY, FONT, TEXT_COLOR, "Host", true);
        this.hostButton.setScale(new Vector3f(0.1f, 0.05f, 1));
        this.hostButton.setPosition(new Vector3f(0, 0, 0));
        this.hostButton.getOnPress().add(() -> new Sound(CLICK_SOUND).register());
        this.hostButton.getOnClick().add(() -> getUniverse().startServer());

        this.optionsButton = new EntityUITextButton(BUTTON_BACKGROUND, BROWSE_OVERLAY, PRESS_OVERLAY, FONT, TEXT_COLOR, "Options", true);
        this.optionsButton.setScale(new Vector3f(0.1f, 0.05f, 1));
        this.optionsButton.setPosition(new Vector3f(0, -0.25f, 0));
        this.optionsButton.getOnPress().add(() -> new Sound(CLICK_SOUND).register());

        this.exitButton = new EntityUITextButton(BUTTON_BACKGROUND, BROWSE_OVERLAY, PRESS_OVERLAY, FONT, TEXT_COLOR, "Exit", true);
        this.exitButton.setScale(new Vector3f(0.1f, 0.05f, 1));
        this.exitButton.setPosition(new Vector3f(0, -0.5f, 0));
        this.exitButton.getOnPress().add(() -> new Sound(CLICK_SOUND).register());
        this.exitButton.getOnClick().add(() -> getUniverse().stop());

        EntityUIEditBox editBox = new EntityUIEditBox(new File("interface/button.png"), new File("interface/editbox_frame.png"), new File("interface/editbox_pointer.png"), new File("fonts/default.png"), new Vector4f(1, 1, 1, 1), true);
        editBox.setScale(new Vector3f(0.2f, 0.05f, 1));
        editBox.setPosition(new Vector3f(0, 0.5f, 0));
        //editBox.register();

        EntityUIRadioBox radioBox = new EntityUIRadioBox(
                new File("interface/ui_background.png"),
                new File("interface/browse_overlay.png"),
                new File("interface/press_overlay.png"),
                new File("interface/checkbox_on.png"),
                new File("interface/checkbox_off.png"),
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
                new File("interface/combo_drop.png"),
                new File("interface/browse_overlay.png"),
                new File("interface/press_overlay.png"),
                new File("interface/combo_background.jpg"),
                new File("interface/browse_overlay.png"),
                new File("interface/press_overlay.png"),
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
                new File("interface/scrollbar_background.jpg"),
                new File("interface/scrollbar_bar.png"),
                new File("interface/browse_overlay.png"),
                new File("interface/press_overlay.png"),
                new File("interface/scrollbar_up.png"),
                new File("interface/browse_overlay.png"),
                new File("interface/press_overlay.png"),
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
        getExitButton().register();
        getResumeButton().register();
        getConnectButton().register();
        getHostButton().register();
        getOptionsButton().register();
        setActive(true);
    }

    @Override
    public void unregister() {
        super.unregister();
        getExitButton().unregister();
        getResumeButton().unregister();
        getConnectButton().unregister();
        getHostButton().unregister();
        getOptionsButton().unregister();
        setActive(false);
    }

    public void toggle() {
        if (isActive()) {
            unregister();
        } else {
            register();
        }
    }
}
